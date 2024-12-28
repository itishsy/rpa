package com.seebon.rpa.service.handle;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.tool.ElementUtil;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.SessionKeep;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotClient;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.RobotClientMapper;
import com.seebon.rpa.repository.mapper.RobotTaskQueueMapper;
import com.seebon.rpa.repository.mapper.RobotTaskSessionKeepMapper;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.FileStorage;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SessionKeepHandle {
    @Autowired
    private RobotTaskSessionKeepMapper taskSessionKeepMapper;
    @Autowired
    private RobotTaskQueueMapper taskQueueMapper;
    @Autowired
    private RobotClientMapper clientMapper;
    @Autowired
    private RobotCommonService commonService;
    @Autowired
    private RpaDesignService designService;
    @Autowired
    private RobotContext ctx;
    @Autowired
    private DriverTarget driverTarget;
    @Autowired
    private ElementUtil elementUtil;
    @Autowired
    private SessionKeep sessionKeep;

    private static boolean refreshDevSessionDriver = false;
    private static final Integer retryNum = 3;
    private static final Integer maxKeepTime = 24;
    private static final Integer maxKeepNum = 20;

    public void refreshProdSessionDriver() {
        List<RobotTaskSessionKeepVO> allList = taskSessionKeepMapper.selectLoginStatus();
        if (CollectionUtils.isEmpty(allList)) {
            log.info("【session维持】 没有会话维持任务.");
            return;
        }
        List<RobotTaskSessionKeepVO> list = allList.stream().filter(vo -> vo.getLoginStatus() != null && vo.getLoginStatus() == 1).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) {
            log.info("【session维持】 没有已登录任务.");
            return;
        }
        Integer maxKeepNum = this.getMaxKeepNum();
        log.info("【session维持】 开始刷新远程浏览器,已登录任务条数：" + list.size() + ", 最大维持数量：" + maxKeepNum);
        List<RobotTaskSessionKeepVO> sharePortList = list.stream().filter(vo -> vo.getSharePort() != null).collect(Collectors.toList());
        Map<Integer, List<RobotTaskSessionKeepVO>> sharePortMap = sharePortList.stream().collect(Collectors.groupingBy(vo -> vo.getSharePort()));
        Integer keepNum = 0;
        for (RobotTaskSessionKeepVO task : list) {
            if (this.isRun(task, allList)) {
                continue;
            }
            if (task.getRetryNum() == null) {
                task.setRetryNum(retryNum);
            }
            if (task.getMaxKeepTime() == null) {
                task.setMaxKeepTime(maxKeepTime);
            }
            ChromeDriver remoteDriver = sessionKeep.getRemoteDriver(task);
            try {
                String debuggerAddress = "127.0.0.1:" + Convert.getPort(task);
                boolean isAvailable = Convert.isAddressAvailable(debuggerAddress);
                if (!isAvailable) {
                    taskSessionKeepMapper.updateStatus(task.getId(), 1, null);
                    if (task.getSharePort() != null) {
                        taskSessionKeepMapper.updateStatusSharePort(task.getSharePort(), 1, null);
                    }
                    this.sysTaskSessionKeep(task.getId());
                    sessionKeep.removeDriver(task);
                    this.log(task, "不可用.");
                    continue;
                }
                if (this.isRun(task, allList)) {
                    continue;
                }
                if (remoteDriver == null) {
                    System.setProperty("webdriver.chrome.driver", RobotContext.workPath + "\\driver\\chromedriver.exe");
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.setExperimentalOption("debuggerAddress", debuggerAddress);
                    remoteDriver = new ChromeDriver(chromeOptions);
                    sessionKeep.setRemoteDriver(task, remoteDriver);
                    this.log(task, "创建新的远程浏览器.");
                }
                if (this.isRun(task, allList)) {
                    continue;
                }
                boolean isKeep = false;
                List<RobotTaskSessionKeepVO> sharePorts = sharePortMap.get(Convert.getPort(task));
                if (CollectionUtils.isNotEmpty(sharePorts)) {
                    for (RobotTaskSessionKeepVO sharePort : sharePorts) {
                        boolean flag = this.isKeep(remoteDriver, sharePort, 1, allList);
                        if (flag) {
                            isKeep = true;
                            break;
                        }
                    }
                } else {
                    isKeep = this.isKeep(remoteDriver, task, 1, allList);
                }
                this.log(task, "校验维持状态：isKeep=" + isKeep);
                if (this.isRun(task, allList)) {
                    continue;
                }
                if (isKeep) {
                    //维持数量校验
                    if (keepNum > maxKeepNum) {
                        String comment = "已维持会话数量：" + keepNum + " 大于允许最大值：" + maxKeepNum + " 会话维持数量已达到上限,并退出浏览器";
                        taskSessionKeepMapper.updateStatus(task.getId(), 1, comment);
                        if (task.getSharePort() != null) {
                            taskSessionKeepMapper.updateStatusSharePort(task.getSharePort(), 1, comment);
                        }
                        this.sysTaskSessionKeep(task.getId());
                        this.quit(remoteDriver, task, allList);
                        this.log(task, comment);
                        continue;
                    }
                    //维持时间校验
                    long betweenHour = 0L;
                    if (task.getStartKeepTime() != null) {
                        betweenHour = DateUtil.between(task.getStartKeepTime(), new Date(), DateUnit.HOUR);
                        if (betweenHour > task.getMaxKeepTime()) {
                            String comment = "已维持会话时间：" + betweenHour + "小时 大于最大维持会话时间：" + task.getMaxKeepTime() + "小时, 开始维持时间：" + DateUtil.formatDateTime(task.getStartKeepTime()) + ",会话维持时间已超时,并退出浏览器";
                            taskSessionKeepMapper.updateStatus(task.getId(), 1, comment);
                            if (task.getSharePort() != null) {
                                taskSessionKeepMapper.updateStatusSharePort(task.getSharePort(), 1, comment);
                            }
                            this.sysTaskSessionKeep(task.getId());
                            this.quit(remoteDriver, task, allList);
                            this.log(task, comment);
                            continue;
                        }
                    }
                    elementUtil.releaseToParent(remoteDriver);
                    taskSessionKeepMapper.keepSuccess(task.getId(), 3);
                    if (task.getSharePort() != null) {
                        taskSessionKeepMapper.keepSuccessSharePort(task.getId(), 3, task.getSharePort());
                    }
                    this.sysTaskSessionKeep(task.getId());
                    keepNum = keepNum + 1;
                    this.log(task, "会话维持中,已维持会话数量：" + keepNum + ",已维持会话时间：" + betweenHour + "小时,isKeep=" + isKeep);
                } else {
                    elementUtil.releaseToParent(remoteDriver);
                    Integer fileId = this.fileUpload(remoteDriver, task);
                    taskSessionKeepMapper.keepFail(task.getId(), 2, fileId);
                    if (task.getSharePort() != null) {
                        taskSessionKeepMapper.keepFailSharePort(task.getSharePort(), 2, fileId);
                    }
                    this.sysTaskSessionKeep(task.getId());
                    ChromeDriver targetDriver = sessionKeep.getTargetDriver(task);
                    this.quit(remoteDriver, task, allList);
                    this.quit(targetDriver, task, allList);
                    sessionKeep.removeDriver(task);
                    this.log(task, "会话已丢失,并退出浏览器.isKeep=" + isKeep);
                }
            } catch (Exception e) {
                log.error("【session维持】 发生了异常。" + e.getMessage() + ",浏览器端口：" + Convert.getPort(task), e);
            }
        }
    }

    private boolean isKeep(ChromeDriver remoteDriver, RobotTaskSessionKeepVO task, Integer retryNum, List<RobotTaskSessionKeepVO> allList) {
        boolean isKeep = this.checkKeep(remoteDriver, task, retryNum, allList);
        if (isKeep) {
            return isKeep;
        }
        if (task.getRetryNum() != null && task.getRetryNum() > 0 && task.getRetryNum() >= (retryNum + 1)) {
            return isKeep(remoteDriver, task, retryNum + 1, allList);
        }
        return isKeep;
    }

    private boolean checkKeep(ChromeDriver remoteDriver, RobotTaskSessionKeepVO task, Integer retryNum, List<RobotTaskSessionKeepVO> allList) {
        try {
            if (this.isRun(task, allList)) {
                return true;
            }
            if (StringUtils.isNotBlank(task.getUrl())) {
                remoteDriver.get(task.getUrl());
                this.log(task, "访问页面成功,总重试次数：" + task.getRetryNum() + ",当前重试次数：" + retryNum);
            }
        } catch (Exception e) {
            this.log(task, "访问页面异常." + e.getMessage());
        }
        try {
            if (this.isRun(task, allList)) {
                return true;
            }
            if (remoteDriver.getWindowHandles().size() > 0) {
                remoteDriver.switchTo();
            }
            remoteDriver.navigate().refresh();
            this.log(task, "刷新页面成功,总重试次数：" + task.getRetryNum() + ",当前重试次数：" + retryNum);
        } catch (Exception e) {
            this.log(task, "刷新页面异常." + e.getMessage());
        }
        try {
            if (this.isRun(task, allList)) {
                return true;
            }
            WebDriverWait wait = new WebDriverWait(remoteDriver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.alertIsPresent());
            org.openqa.selenium.Alert alert = remoteDriver.switchTo().alert();
            if (alert != null) {
                alert.accept();
            }
        } catch (Exception e) {
            this.log(task, "接受消息弹窗异常,此异常可以忽略.");
        }
        try {
            if (this.isRun(task, allList)) {
                return true;
            }
            if (StringUtils.isNotBlank(task.getLoseXpath())) {
                WebElement loseElement = elementUtil.findByXpath(remoteDriver, task.getLoseXpath(), 60);
                if (loseElement != null) {
                    this.log(task, "会话丢失xpath校验成功");
                    return false;
                }
            }
        } catch (Exception e) {
            this.log(task, "校验会话丢失xpath异常." + e.getMessage());
        }
        try {
            if (this.isRun(task, allList)) {
                return true;
            }
            WebElement element = elementUtil.findByXpath(remoteDriver, task.getXpath(), 60);
            if (element != null) {
                this.log(task, "会话维持xpath校验成功");
                if (StringUtils.isNotBlank(task.getMatchExpress())) {
                    String text = Convert.getText(element);
                    Map<String, Object> vars = Maps.newHashMap();
                    vars.put("elementText", text);
                    boolean matchFlag = ELParser.parse(text, vars, Boolean.class);
                    if (matchFlag) {
                        this.log(task, "会话维持xpath匹配表达式校验成功");
                        return true;
                    }
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            this.log(task, "校验会话维持xpath异常." + e.getMessage());
        }
        return false;
    }

    public Integer fileUpload(ChromeDriver remoteDriver, RobotTaskSessionKeep task) {
        if (remoteDriver == null) {
            return null;
        }
        String filePath = Convert.screenShot(task, remoteDriver);
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        String fileId = commonService.fileUpload(new File(filePath));
        return Integer.parseInt(fileId);
    }

    public void sysTaskSessionKeep(Integer keepId) {
        RobotTaskSessionKeep sessionKeep = taskSessionKeepMapper.selectByPrimaryKey(keepId);
        try {
            log.info("回传任务会话维持,sessionKeep=" + JSON.toJSONString(sessionKeep));
            RobotTaskSessionKeep keep = new RobotTaskSessionKeep();
            keep.setId(sessionKeep.getId());
            keep.setStatus(sessionKeep.getStatus());
            keep.setFileId(sessionKeep.getFileId());
            keep.setMachineCode(sessionKeep.getMachineCode());
            keep.setStartKeepTime(sessionKeep.getStartKeepTime());
            keep.setEndKeepTime(sessionKeep.getEndKeepTime());
            keep.setTaskCode(sessionKeep.getTaskCode());
            keep.setDeclareSystem(sessionKeep.getDeclareSystem());
            keep.setClientId(sessionKeep.getClientId());
            keep.setSharePort(sessionKeep.getSharePort());
            keep.setComment(sessionKeep.getComment());
            log.info("回传任务会话维持,keep=" + JSON.toJSONString(keep));
            designService.updateTaskSessionKeep(keep);
        } catch (Exception e) {
            log.error("回传任务会话维持失败,taskCode=" + sessionKeep.getTaskCode(), e);
        }
    }

    public void refreshDevSessionDriver() {
        if (!refreshDevSessionDriver) {
            return;
        }
        Map<Integer, String> remoteDebugPorts = ctx.get(RobotConstant.REMOTE_DEBUG_PORT);
        if (MapUtils.isEmpty(remoteDebugPorts)) {
            return;
        }
        for (Integer port : remoteDebugPorts.keySet()) {
            String taskCode = remoteDebugPorts.get(port);
            RobotTaskQueueVO queueVO = RobotConstant.taskRunMap.get(taskCode);
            if (queueVO != null) {
                log.info("taskCode=" + taskCode + " 任务正在执行,不刷新浏览器");
                continue;
            }
            try {
                String debuggerAddress = "127.0.0.1:" + port;
                boolean isAvailable = Convert.isAddressAvailable(debuggerAddress);
                if (!isAvailable) {
                    driverTarget.removePort(port);
                    continue;
                }
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("debuggerAddress", debuggerAddress);
                ChromeDriver driver = new ChromeDriver(chromeOptions);
                driver.navigate().refresh();//刷新
                log.info("【刷新存活的浏览器】 taskCode=" + taskCode + " 刷新浏览器成功");
            } catch (Exception e) {
                log.error("【刷新存活的浏览器】发生了异常。" + e.getMessage());//不打印异常堆栈
                driverTarget.removePort(port);
            }
        }
    }

    private boolean isRun(RobotTaskSessionKeepVO task, List<RobotTaskSessionKeepVO> allList) {
        if (MapUtils.isEmpty(RobotConstant.taskRunMap)) {
            return false;
        }
        RobotTaskQueueVO queueVO = RobotConstant.taskRunMap.get(task.getTaskCode());
        if (queueVO != null) {
            this.log(task, "任务正在执行,不刷新浏览器");
            return true;
        }
        Integer sharePort = this.getSharePort(allList);
        log.info("【session维持】 当前执行任务的共享端口号：sharePort=" + sharePort);
        if (sharePort != null && task.getSharePort() != null && sharePort.intValue() == task.getSharePort().intValue()) {
            this.log(task, "共享端口号的其他任务在执行,不刷新浏览器");
            return true;
        }
        return false;
    }

    public void quit(ChromeDriver driver, RobotTaskSessionKeepVO task, List<RobotTaskSessionKeepVO> allList) {
        if (this.isRun(task, allList)) {
            return;
        }
        Convert.quit(driver, "openInterceptApi");
    }

    private Integer getSharePort(List<RobotTaskSessionKeepVO> allList) {
        if (MapUtils.isEmpty(RobotConstant.taskRunMap)) {
            return null;
        }
        RobotTaskQueueVO queueVO = RobotConstant.taskRunMap.values().stream().findFirst().get();
        if (queueVO == null) {
            return null;
        }
        String executionCode = queueVO.getExecutionCode();
        Example example = new Example(RobotTaskQueue.class);
        example.orderBy("id").desc();
        example.createCriteria().andEqualTo("executionCode", executionCode);
        List<RobotTaskQueue> taskQueueList = taskQueueMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(taskQueueList)) {
            return null;
        }
        RobotTaskQueue taskQueue = taskQueueList.get(0);
        for (RobotTaskSessionKeepVO shareTask : allList) {
            if (taskQueue.getAppCode().equals(shareTask.getAppCode()) && taskQueue.getTaskCode().equals(shareTask.getTaskCode()) && taskQueue.getDeclareSystem().equals(shareTask.getDeclareSystem())) {
                return shareTask.getSharePort();
            }
        }
        return null;
    }

    private Integer getMaxKeepNum() {
        String machineCode = FileStorage.loadMachineCodeFromDisk();
        Example example = new Example(RobotClient.class);
        example.createCriteria().andEqualTo("machineCode", machineCode);
        RobotClient client = clientMapper.selectOneByExample(example);
        if (client == null || client.getMaxKeepNum() == null) {
            return maxKeepNum;
        }
        return client.getMaxKeepNum();
    }

    private void log(RobotTaskSessionKeepVO task, String msg) {
        log.info("【session维持】 " + task.getAppName() + " " + task.getOrgName() + "-" + task.getAccountNumber() + " " + TplTypeCodeEnum.getNameByCode(task.getDeclareSystem()) + ",浏览器端口：" + Convert.getPort(task) + " " + msg);
    }
}
