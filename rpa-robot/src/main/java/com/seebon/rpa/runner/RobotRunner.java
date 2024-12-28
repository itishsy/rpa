package com.seebon.rpa.runner;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.RobotTaskDTO;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.repository.mapper.*;
import com.seebon.rpa.service.*;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.EncryptUtil;
import com.seebon.rpa.utils.FileStorage;
import com.seebon.rpa.utils.UsbIpUtil;
import com.seebon.rpa.utils.enums.DeclareTypeEnum;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 执行任务
 *
 * @author: xfz
 */
@Slf4j
@Component
public class RobotRunner {
    @Autowired
    private RobotTaskMapper taskMapper;
    @Autowired
    private RobotTaskArgsMapper taskArgsMapper;
    @Autowired
    private RobotAppMapper appMapper;
    @Autowired
    private RobotFlowMapper flowMapper;
    @Autowired
    private RobotFlowStepMapper flowStepMapper;
    @Autowired
    private RobotFlowStepArgsMapper flowStepArgsMapper;
    @Autowired
    private RobotExecutionMapper executionMapper;
    @Autowired
    private RobotExecutionMonitorService executionMonitorService;
    @Autowired
    private RobotExecutionService executionService;
    @Autowired
    private RobotCommonService commonService;
    @Autowired
    private RobotTaskQueueService taskQueueService;
    @Autowired
    private SyncOutService syncOutService;
    @Autowired
    private RobotExecutor robotExecutor;
    @Autowired
    private FlowExecutor flowExecutor;
    @Autowired
    private RobotContext ctx;
    @Autowired
    private RobotTaskSessionKeepService taskSessionKeepService;
    @Autowired
    private RobotAppCaService appCaService;
    @Autowired
    private RobotConfigService configService;

    public void startTask(RobotTaskQueueVO queueVO) {
        log.info("开始执行: 队列参数=" + JSON.toJSONString(queueVO));
        //设置运行缓存
        RobotConstant.taskRunMap.put(queueVO.getTaskCode(), queueVO);
        //当前时间
        Date nowDate = new Date();
        //清除缓存
        ctx.clear();
        try {
            //安装应用证书
            appCaService.installAppCa(queueVO);
            //检查任务
            String cheTask = this.checkTask(queueVO.getTaskCode());
            if (StringUtils.isNotBlank(cheTask)) {
                queueVO.setComment(cheTask);
                log.info(queueVO.getAppName() + cheTask);
                return;
            }
            List<RobotFlowVO> flows = this.getFlows(queueVO);
            if (CollectionUtils.isEmpty(flows)) {
                queueVO.setComment("流程未同步到盒子,请等待.");
                log.info(queueVO.getAppName() + " 流程未同步到盒子,请等待.");
                return;
            }
            //执行一次任务的流程计数器
            ctx.setVariable(RobotConstant.FLOW_NUM_KEY, flows.size());
            for (RobotFlowVO flow : flows) {

                ctx.setVariable(RobotConstant.EXECUTION_FLOW_CODE_KEY, flow.getFlowCode());

                Integer flowNum = ctx.getVariable(RobotConstant.FLOW_NUM_KEY);
                if (flowNum != null && flowNum != 0) {
                    ctx.setVariable(RobotConstant.FLOW_NUM_KEY, flowNum - 1);
                }
                try {
                    RobotTaskDTO taskExit = taskMapper.findByCode(queueVO.getTaskCode());
                    if (taskExit == null) {
                        return;
                    }
                    log.info(queueVO.getAppName() + " " + queueVO.getCompanyName() + " " + queueVO.getDeclareAccount() + " " + flow.getFlowName() + " 开始执行.");

                    //初始化
                    this.initCtxByTask(queueVO, flow);

                    //生成下载目录
                    this.mkDownloadFilePath(queueVO, flow);

                    //默认下载附件
                    this.downloadAttachment(queueVO.getTaskCode());

                    //添加执行记录
                    Integer executionId = executionService.addExecution(queueVO, flow.getFlowCode());

                    //更新执行记录
                    RobotExecution updateExecution = new RobotExecution();
                    updateExecution.setId(executionId);
                    updateExecution.setStatus(1);
                    try {
                        this.startFlow(flow);
                    } catch (Exception e) {
                        log.error(queueVO.getAppName() + " " + queueVO.getCompanyName() + " " + queueVO.getDeclareAccount() + " " + flow.getFlowName() + " 执行异常.", e);
                        updateExecution.setStatus(0);
                        if (e.getMessage() != null && e.getMessage().length() > 2000) {
                            updateExecution.setError(e.getMessage().substring(0, 1999));
                        }
                        syncOutService.loginSuccess(flow.getTagCode(), 2, "登录失败");
                    } finally {
                        updateExecution.setEndTime(new Date());
                        executionMapper.updateByPrimaryKeySelective(updateExecution);
                    }
                    log.info(queueVO.getAppName() + " " + queueVO.getCompanyName() + " " + queueVO.getDeclareAccount() + " " + flow.getFlowName() + " 完成执行.");
                } finally {
                    ctx.sessionKeep();
                }
            }
        } finally {
            //释放执行任务缓存
            RobotConstant.taskRunMap.remove(queueVO.getTaskCode());
            //添加监控数据
            executionMonitorService.addMonitor(queueVO, nowDate);
            //更新队列状态
            taskQueueService.updateTaskQueue(queueVO);
            //卸载CA证书
            appCaService.unInstallAppCa(queueVO);
            //卸载usbKey
            this.usbIpDetach();
            log.info("完成执行: 队列参数=" + JSON.toJSONString(queueVO));
        }
    }

    private void usbIpDetach() {
        if (!RobotConstant.USB_KEY_FLAG) {
            log.info("usbKey未挂载成功或不需要挂载.");
            return;
        }
        try {
            String result = UsbIpUtil.detach();
            log.info("卸载usbKey result=" + result);
        } catch (Exception e) {
            log.error("卸载usbKey异常." + e.getMessage(), e);
        } finally {
            RobotConstant.USB_KEY_FLAG = false;
        }
    }

    private void startFlow(RobotFlowVO flow) {
        if (StringUtils.isBlank(flow.getTemplateFlowCode())) {
            List<RobotFlowStep> steps = flowStepMapper.findSteps(StringUtils.defaultIfBlank(flow.getRelationFlowCode(), flow.getFlowCode()));
            if (Convert.isContains(steps)) {
                flowExecutor.start(steps);
            } else {
                robotExecutor.start(steps);
            }
        } else {
            List<RobotFlowStep> steps = flowStepMapper.findSteps(flow.getTemplateFlowCode());
            List<RobotFlowStepArgs> stepArgs = flowStepArgsMapper.selectAllByFlowCode(flow.getFlowCode());
            Map<String, RobotFlowStepArgs> stepArgsMap = Maps.newHashMap();
            for (RobotFlowStepArgs args : stepArgs) {
                stepArgsMap.put(args.getStepCode(), args);
            }
            for (RobotFlowStep step : steps) {
                if (stepArgsMap.containsKey(step.getStepCode())) {
                    step.setTargetArgs(stepArgsMap.get(step.getStepCode()).getTargetArgs());
                    step.setActionArgs(stepArgsMap.get(step.getStepCode()).getActionArgs());
                }
            }
            flowExecutor.start(steps);
        }
    }

    private void initCtxByTask(RobotTaskQueueVO queueVO, RobotFlowVO flow) {
        ctx.init();
        RobotApp app = appMapper.findApp(queueVO.getTaskCode());
        if (app != null && StringUtils.isNotBlank(app.getAppArgs())) {
            JSONObject jsonObject = JSONObject.parseObject(app.getAppArgs());
            for (String key : jsonObject.keySet()) {
                ctx.setVariable(key, jsonObject.get(key));
            }
            ctx.setVariable("sessionKeep", app.getSessionKeep());
            //session维持
            if (app.getSessionKeep() != null && app.getSessionKeep() == 1) {
                this.closeWindow();
            }
        }

        Example e = new Example(RobotTask.class);
        e.createCriteria().andEqualTo("taskCode", queueVO.getTaskCode());
        RobotTask task = taskMapper.selectOneByExample(e);
        ctx.setVariable("taskStatus", task.getStatus());
        ctx.setVariable("appCode", task.getAppCode());
        ctx.setVariable("taskCode", task.getTaskCode());
        ctx.setVariable("clientId", task.getClientId());
        ctx.setVariable("machineCode", task.getMachineCode());
        ctx.setVariable("taskId", task.getId());
        ctx.setVariable("queueItem", queueVO.getQueueItem());

        List<RobotTaskArgs> taskArgsList = taskMapper.findArgs(queueVO.getTaskCode());
        if (CollectionUtils.isNotEmpty(taskArgsList)) {
            for (RobotTaskArgs taskArgs : taskArgsList) {
                String argsType = taskArgs.getArgsType();
                String argsValue = taskArgs.getArgsValue();
                if (StringUtils.isNotBlank(argsType) && "password".equals(argsType) && StringUtils.isNotBlank(argsValue)) {
                    try {
                        String aesVal = EncryptUtil.aesDecrypt(taskArgs.getArgsValue()); // 密码解密
                        ctx.setVariable(taskArgs.getArgsKey(), aesVal);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        log.error("密码解密失败：{}", e1.getMessage(), e1);
                        ctx.setVariable(taskArgs.getArgsKey(), argsValue);
                    }
                } else {
                    ctx.setVariable(taskArgs.getArgsKey(), argsValue);
                }
            }
        }
        //绑定的USB
        ctx.setVariable("usbMap", FileStorage.loadPortFromDisk());
        //执行批次
        String uuid = UUIDGenerator.uuidStringWithoutLine();
        ctx.setVariable(RobotConstant.BATCH_CODE, uuid);
        ctx.setVariable(RobotConstant.INST_ID, uuid);
        //执行码
        ctx.setVariable(RobotConstant.EXECUTION_CODE_KEY, queueVO.getExecutionCode());
        //流程编码
        ctx.setVariable("flowCode", flow.getFlowCode());
        ctx.setVariable("tplTypeCode", flow.getDeclareSystem());
        ctx.setVariable("declareType", flow.getServiceItem());
        ctx.setVariable("runSupport", flow.getRunSupport());
        ctx.setVariable("loginType", ctx.getLoginType());
        ctx.setVariable("businessTypeInt", Convert.getBusinessType(ctx.get("businessType")));
        ctx.setVariable("accountNumber", ctx.getAccountNumber());
        if (StringUtils.isNotBlank(flow.getTemplateFlowCode())) {
            ctx.setVariable("templateFlowCode", flow.getTemplateFlowCode());
        }
        if (queueVO.getQueueItem() == 11) {
            ctx.setVariable(RobotConstant.LOGIN_FLOW_FLAG, "1");
        }
        List<RobotConfig> configList = configService.listConfig();
        if (CollectionUtils.isNotEmpty(configList)) {
            for (RobotConfig config : configList) {
                ctx.setVariable(config.getKey(), config.getContext());
            }
        }
    }

    private List<RobotFlowVO> getFlows(RobotTaskQueueVO queueVO) {
        List<Integer> serviceItemList = queueVO.getTaskQueueItemList().stream().map(vo -> vo.getServiceItem()).distinct().collect(Collectors.toList());
        //执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：在册名单，8：费用明细，9：政策通知 10：基数申报 11：登录 12：查审核结果
        //服务项目，1：增员，2：减员，3：调基，5：补缴, 6：缴费 7：名册名单 8：费用明细 9：政策通知 10：基数申报 11：登录  12：查审核结果
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskCode", queueVO.getTaskCode());
        params.put("declareSystem", queueVO.getDeclareSystem());
        if (queueVO.getQueueItem() != 11) {
            params.put("serviceItemList", serviceItemList);
        }
        if (queueVO.getQueueItem() == 11) {
            params.put("tagCode", "10018008");//登录流程
        } else if (queueVO.getQueueItem() == 12) {
            params.put("tagCode", "10018010");//核验流程
        }
        List<RobotFlowVO> appFlows = flowMapper.findFlows(params);
        if (CollectionUtils.isNotEmpty(appFlows)) {
            //若1：增减员（增员、减员、调基、补缴），则增加执行回盘流程
            if (queueVO.getQueueItem() == 1 || queueVO.getQueueItem() == 12) {
                params.put("tagCode", "10018009");//回盘流程
                params.remove("serviceItemList");
                List<RobotFlowVO> returnFlows = flowMapper.findFlows(params);
                if (CollectionUtils.isNotEmpty(returnFlows)) {
                    appFlows.addAll(returnFlows);
                }
            }
        }
        return appFlows;
    }

    private void downloadAttachment(String taskCode) {
        List<RobotTaskArgs> taskArgsList = taskMapper.findArgs(taskCode);
        if (CollectionUtils.isEmpty(taskArgsList)) {
            return;
        }
        String dataPath = ctx.get(RobotConstant.DATA_PATH);
        String accountNumber = ctx.getAccountNumber();
        for (RobotTaskArgs taskArgs : taskArgsList) {
            if (!Lists.newArrayList("photoUpload", "fileUpload").contains(taskArgs.getArgsType())) {
                continue;
            }
            try {
                String UUID = UUIDGenerator.uuidStringWithoutLine();
                String path = dataPath + "\\" + accountNumber + "_" + taskArgs.getArgsKey() + "_" + UUID;
                CloseableHttpResponse response = commonService.downloadFile(taskArgs.getArgsValue());
                String newPath = Convert.resp2File(response, path);
                if (StringUtils.isNotBlank(newPath)) {
                    ctx.setVariable(taskArgs.getArgsKey() + "Url", newPath);
                }
            } catch (Exception e) {
                log.error("下载附件异常：" + e.getMessage(), e);
            }
        }
    }

    private String mkDownloadFilePath(RobotTaskQueueVO queueVO, RobotFlowVO flow) {
        String sharePortPath = this.getSharePortPath();
        String workPath = ctx.getVariable("workPath");
        String addrName = ctx.getVariable("addrName");
        String businessType = ctx.getVariable("businessType");
        String instId = ctx.getVariable("instId");
        String accountNumber = ctx.getAccountNumber();

        String downloadPath = Convert.appendPath(workPath.replace("\\\\", "\\"), "cache", addrName);
        if ("1001001".equals(businessType)) {
            downloadPath = Convert.appendPath(downloadPath, "社保", StringUtils.defaultIfBlank(sharePortPath, accountNumber));
        } else if ("1001002".equals(businessType)) {
            downloadPath = Convert.appendPath(downloadPath, "公积金", StringUtils.defaultIfBlank(sharePortPath, accountNumber));
        }
        FileUtil.mkdir(downloadPath);
        ctx.setVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH, downloadPath);
        ctx.setVariable(RobotConstant.DATA_PATH, downloadPath);

        //固定一个报盘存储文件路径
        String declareName = DeclareTypeEnum.getNameByCode(ctx.get("declareType"));
        if (StringUtils.isNotBlank(declareName)) {
            String tplTypeName = TplTypeCodeEnum.getNameByCode(ctx.get("tplTypeCode"));
            String fileSuffix = this.getFileSuffix(queueVO, flow);
            String filePath = Convert.appendPath(downloadPath, accountNumber + "_" + tplTypeName + "_" + declareName + "_" + instId + fileSuffix);
            log.info("报盘文件路径: filePath=" + filePath);
            ctx.setVariable(RobotConstant.FILE_PATH, filePath);
        }
        return downloadPath;
    }

    private String getSharePortPath() {
        RobotTaskSessionKeep sessionKeep = taskSessionKeepService.getSessionKeep();
        if (sessionKeep == null || sessionKeep.getSharePort() == null) {
            return null;
        }
        String year = DateUtil.format(new Date(), "yyyy");
        return Convert.appendPath(year, sessionKeep.getSharePort().toString());
    }

    private void closeWindow() {
        try {
            WebDriver driver = ctx.getDriver();
            if (driver == null) {
                return;
            }
            Set<String> handles = driver.getWindowHandles();
            if (CollectionUtils.isEmpty(handles) || handles.size() <= 1) {
                return;
            }
            String currentHandle = driver.getWindowHandle();
            driver.close();
            for (String handle : handles) {
                if (!handle.equals(currentHandle)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
        } catch (Exception ex) {
            log.error("关闭窗口失败：" + ex.getMessage(), ex);
        }
    }

    private String getFileSuffix(RobotTaskQueueVO queueVO, RobotFlowVO flow) {
        if (!Lists.newArrayList(1, 12).contains(queueVO.getQueueItem())) {
            return null;
        }
        if (CollectionUtils.isEmpty(queueVO.getTaskQueueItemList())) {
            return null;
        }
        RobotTaskQueueItem queueItem = queueVO.getTaskQueueItemList().stream().filter(vo -> vo.getServiceItem().equals(flow.getServiceItem())).findFirst().orElse(null);
        if (queueItem == null || StringUtils.isBlank(queueItem.getItemArgs())) {
            return null;
        }
        JSONObject itemArgs = JSONObject.parseObject(queueItem.getItemArgs());
        return itemArgs.getString("fileSuffix");
    }

    private String checkTask(String taskCode) {
        Example example = new Example(RobotTask.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        int taskCount = taskMapper.selectCountByExample(example);
        if (taskCount <= 0) {
            return "任务未同步到盒子,请等待.";
        }
        example = new Example(RobotTaskArgs.class);
        example.createCriteria().andEqualTo("taskCode", taskCode);
        int taskArgsCount = taskArgsMapper.selectCountByExample(example);
        if (taskArgsCount <= 0) {
            return "任务参数未同步到盒子,请等待.";
        }
        return null;
    }
}
