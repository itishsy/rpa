package com.seebon.rpa.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.OfferLabelType;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.RobotTaskDTO;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.repository.mapper.*;
import com.seebon.rpa.runner.FlowExecutor;
import com.seebon.rpa.runner.RobotExecutor;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.service.RobotRunService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.EncryptUtil;
import com.seebon.rpa.utils.FileStorage;
import com.seebon.rpa.utils.enums.DeclareTypeEnum;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotRunServiceImpl implements RobotRunService {
    @Autowired
    private RobotFlowMapper flowMapper;
    @Autowired
    private RobotFlowStepMapper flowStepMapper;
    @Autowired
    private RobotFlowTemplateMapper flowTemplateMapper;
    @Autowired
    private RobotExecutionDetailMapper executionDetailMapper;
    @Autowired
    private RobotTaskMapper taskMapper;
    @Autowired
    private RobotExecutionDetailMapper detailMapper;
    @Autowired
    private RobotExecutionMapper executionMapper;
    @Autowired
    private RobotFlowStepArgsMapper flowStepArgsMapper;
    @Autowired
    private RpaDesignService rpaDesignService;
    @Autowired
    private RobotCommonService robotCommonService;
    @Autowired
    private RobotAppMapper appMapper;
    @Autowired
    private RobotExecutor robotExecutor;
    @Autowired
    private FlowExecutor flowExecutor;
    @Autowired
    private RobotContext ctx;

    @Override
    public List<RobotExecutionDetail> taskFlow(String taskCode, String flowCode) {
        //执行流水号
        String executionCode = UUIDGenerator.uuidStringWithoutLine();
        try {
            //流程
            RobotFlowVO flowVO = this.getByFlowCode(flowCode);
            //执行步骤
            List<RobotFlowStep> steps = flowStepMapper.findSteps(StringUtils.defaultIfBlank(flowVO.getRelationFlowCode(), flowCode));
            if (steps != null && steps.size() > 0) {
                //初始化
                this.initCtxByTask(taskCode, executionCode, flowVO, Dict.create());

                //校验是否有申报数据
                Dict dict = this.checkDeclareOffer(flowVO, null);
                if (!dict.getBool("isRunFlow")) {
                    return this.getExecutionDetail(flowVO, executionCode, "没有数据，略过流程执行.");
                }

                //文件存储路径
                this.mkDownloadFilePath(dict);

                //默认下载附件
                this.downloadAttachment(taskCode);

                //开始
                robotExecutor.start(steps);
            }
        } catch (Exception e) {
            log.error("指令执行异常." + e.getMessage(), e);
        } finally {
            ctx.release();
        }
        return this.getExecutionDetail(executionCode);
    }

    @Override
    public List<RobotExecutionDetail> startFlow(String taskCode, String flowCode) {
        //执行流水号
        String executionCode = UUIDGenerator.uuidStringWithoutLine();
        try {
            //流程模板
            RobotFlowTemplate flowTemplate = flowTemplateMapper.selectByFlowCode(flowCode);

            //流程
            RobotFlowVO flowVO = this.getByFlowCode(flowCode);

            //初始化
            this.initCtxByTask(taskCode, executionCode, flowVO, Dict.create());

            //校验是否有申报数据
            Dict dict = this.checkDeclareOffer(flowVO, flowTemplate);
            if (!dict.getBool("isRunFlow")) {
                return this.getExecutionDetail(flowVO, executionCode, "没有数据，略过流程执行.");
            }

            //文件存储路径
            this.mkDownloadFilePath(dict);

            //默认下载附件
            this.downloadAttachment(taskCode);

            //流程
            if (flowTemplate == null) {
                List<RobotFlowStep> steps = flowStepMapper.findSteps(StringUtils.defaultIfBlank(flowVO.getRelationFlowCode(), flowCode));
                if (Convert.isContains(steps)) {
                    flowExecutor.start(steps);
                } else {
                    robotExecutor.start(steps);
                }
            } else {
                flowVO.setTemplateFlowCode(flowTemplate.getTemplateFlowCode());
                List<RobotFlowStep> steps = flowStepMapper.findSteps(flowTemplate.getTemplateFlowCode());

                List<RobotFlowStepArgs> stepArgs = flowStepArgsMapper.selectAllByFlowCode(flowCode);
                Map<String, RobotFlowStepArgs> usingTemplateSteps = new HashMap<>();
                if (stepArgs != null && stepArgs.size() > 0) {
                    for (RobotFlowStepArgs args : stepArgs) {
                        usingTemplateSteps.put(args.getStepCode(), args);
                    }
                }

                for (RobotFlowStep step : steps) {
                    step.setFlowCode(flowCode);
                    if (usingTemplateSteps.containsKey(step.getStepCode())) {
                        step.setTargetArgs(usingTemplateSteps.get(step.getStepCode()).getTargetArgs());
                        step.setActionArgs(usingTemplateSteps.get(step.getStepCode()).getActionArgs());
                    }
                }
                flowExecutor.start(steps);
            }
        } catch (Exception e) {
            log.error("指令执行异常." + e.getMessage(), e);
        } finally {
            ctx.release();
        }
        return this.getExecutionDetail(executionCode);
    }

    @Override
    public List<RobotExecutionDetail> startFlowStep(String taskCode, String flowCode, String stepCode) {
        //执行流水号
        String executionCode = UUIDGenerator.uuidStringWithoutLine();
        try {
            //流程
            RobotFlowVO flowVO = this.getByFlowCode(flowCode);

            //初始化
            this.initCtxByTask(taskCode, executionCode, flowVO, Dict.create());

            //校验是否有申报数据
            Dict dict = this.checkDeclareOffer(flowVO, null);
            if (!dict.getBool("isRunFlow")) {
                return this.getExecutionDetail(flowVO, executionCode, "没有数据，略过流程执行.");
            }

            //文件存储路径
            this.mkDownloadFilePath(dict);

            //默认下载附件
            this.downloadAttachment(taskCode);

            //流程
            List<RobotFlowStep> steps = flowStepMapper.findSteps(StringUtils.defaultIfBlank(flowVO.getRelationFlowCode(), flowCode));
            if (CollectionUtils.isEmpty(steps)) {
                return Lists.newArrayList();
            }
            //当前步骤
            RobotFlowStep step = steps.stream().filter(s -> s.getStepCode().equals(stepCode)).findFirst().get();
            //步骤列表
            List<RobotFlowStep> stepList = steps.stream().filter(s -> s.getNumber() >= step.getNumber()).collect(Collectors.toList());
            //排序
            List<RobotFlowStep> sortStepList = stepList.stream().sorted(Comparator.comparingInt(RobotFlowStep::getNumber)).collect(Collectors.toList());
            //执行
            robotExecutor.start(sortStepList);
        } catch (Exception e) {
            log.error("指令执行异常." + e.getMessage(), e);
        } finally {
            ctx.release();
        }
        return this.getExecutionDetail(executionCode);
    }

    @Override
    public void startTestFlowStep(String taskCode, String flowCode, String executionCode, List<RobotFlowStep> steps) {
        //任务
        RobotTaskDTO task = taskMapper.findByCode(taskCode);
        try {
            //流程
            RobotFlowVO flowVO = rpaDesignService.getByFlowCode(flowCode);
            //初始化
            this.initCtxByTask(taskCode, executionCode, flowVO, Dict.create());
            //校验是否在执行流程
            if (ctx.isRun()) {
                List<RobotExecutionDetail> details = this.getExecutionDetail(flowVO, executionCode, "盒子在执行其他流程，请稍后在试.");
                detailMapper.insertList(details);
            }

            //校验是否有申报数据
            Dict dict = this.checkDeclareOffer(flowVO, null);
            if (!dict.getBool("isRunFlow")) {
                List<RobotExecutionDetail> details = this.getExecutionDetail(flowVO, executionCode, "没有数据，略过流程执行.");
                detailMapper.insertList(details);
            }

            //文件存储路径
            this.mkDownloadFilePath(dict);

            //默认下载附件
            this.downloadAttachment(taskCode);

            //添加执行记录
            RobotExecution addExecution = new RobotExecution();
            addExecution.setExecutionCode(executionCode);
            addExecution.setClientId(task.getClientId());
            addExecution.setTaskCode(task.getTaskCode());
            addExecution.setMachineCode(FileStorage.loadMachineCodeFromDisk());
            addExecution.setFlowCode(flowCode);
            addExecution.setStartTime(new Date());
            addExecution.setStatus(1);
            try {
                robotExecutor.start(steps);
            } catch (Exception e) {
                addExecution.setStatus(0);
                if (e.getMessage() != null && e.getMessage().length()>2000) {
                    addExecution.setError(e.getMessage().substring(0, 1999));
                }
            } finally {
                addExecution.setEndTime(new Date());
                executionMapper.insertSelective(addExecution);
            }
        } catch (Exception e) {
            log.error("指令执行异常." + e.getMessage(), e);
        } finally {
            //释放内存
            ctx.quit();
        }
    }

    private List<RobotExecutionDetail> getExecutionDetail(String executionCode) {
        Example example = new Example(RobotExecutionDetail.class);
        example.createCriteria().andEqualTo("executionCode", executionCode);
        return executionDetailMapper.selectByExample(example);
    }

    private List<RobotExecutionDetail> getExecutionDetail(RobotFlowVO flowVO, String executionCode, String msg) {
        RobotExecutionDetail detail = new RobotExecutionDetail();
        detail.setExecutionCode(executionCode);
        detail.setFlowCode(flowVO.getFlowCode());
        detail.setStepName("流程：" + flowVO.getFlowName() + " 错误：" + msg);
        detail.setStartTime(new Date());
        detail.setStatus(0);
        detail.setEndTime(new Date());
        detail.setError("流程：" + flowVO.getFlowName() + " 错误：" + msg);
        log.info("流程：" + flowVO.getFlowName() + " 错误：" + msg);
        return Lists.newArrayList(detail);
    }

    private void initCtxByTask(String taskCode, String executionCode, RobotFlowVO flow, Dict dict) {
        ctx.init();
        RobotApp app = appMapper.findApp(taskCode);
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
        e.createCriteria().andEqualTo("taskCode", taskCode);
        RobotTask task = taskMapper.selectOneByExample(e);
        ctx.setVariable("taskStatus", task.getStatus());
        ctx.setVariable("appCode", task.getAppCode());
        ctx.setVariable("taskCode", task.getTaskCode());
        ctx.setVariable("clientId", task.getClientId());
        ctx.setVariable("machineCode", task.getMachineCode());
        ctx.setVariable("taskId", task.getId());

        List<RobotTaskArgs> taskArgsList = taskMapper.findArgs(taskCode);
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
        ctx.setVariable(RobotConstant.EXECUTION_CODE_KEY, executionCode);
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
        String loginFlowFlag = dict.getStr(RobotConstant.LOGIN_FLOW_FLAG);
        if (StringUtils.isNotBlank(loginFlowFlag)) {
            ctx.setVariable(RobotConstant.LOGIN_FLOW_FLAG, loginFlowFlag);
        }
    }

    public Dict checkDeclareOffer(RobotFlowVO flow, RobotFlowTemplate flowTemplate) {
        Dict resultMap = Dict.create().set("isRunFlow", true);
        String flowCode = StringUtils.defaultIfBlank(flow.getRelationFlowCode(), flow.getFlowCode());
        if (flowTemplate != null) {
            flowCode = flowTemplate.getTemplateFlowCode();
        }
        RobotFlowStep flowStep = flowStepMapper.getByActionArgs(flowCode, "generateOffer", "declareMonth");
        if (flowStep != null) {
            return this.checkDeclareData(resultMap, flowStep, flow);
        }
        flowStep = flowStepMapper.getByTargetArgs(flowCode, "httpDownload", "\"method\":\"postDeclare\"");
        if (flowStep != null) {
            return this.checkDeclareData(resultMap, null, flow);
        }
        return resultMap;
    }

    public void downloadAttachment(String taskCode) {
        try {
            List<RobotTaskArgs> taskArgsList = taskMapper.findArgs(taskCode);
            if (CollectionUtils.isEmpty(taskArgsList)) {
                return;
            }
            String dataPath = ctx.get(RobotConstant.DATA_PATH);
            String accountNumber = ctx.getAccountNumber();
            for (RobotTaskArgs taskArgs : taskArgsList) {
                if (Lists.newArrayList("photoUpload", "fileUpload").contains(taskArgs.getArgsType())) {
                    try {
                        String UUID = UUIDGenerator.uuidStringWithoutLine();
                        String path = dataPath + "\\" + accountNumber + "_" + taskArgs.getArgsKey() + "_" + UUID;
                        CloseableHttpResponse response = robotCommonService.downloadFile(taskArgs.getArgsValue());
                        String newPath = Convert.resp2File(response, path);
                        if (StringUtils.isNotBlank(newPath)) {
                            ctx.setVariable(taskArgs.getArgsKey() + "Url", newPath);
                        }
                    } catch (Exception ex) {
                        log.error("下载文件失败：" + ex.getMessage(), ex);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("下载附件失败：" + ex.getMessage(), ex);
        }
    }

    public String mkDownloadFilePath(Dict dict) {
        String workPath = ctx.getVariable("workPath");
        String addrName = ctx.getVariable("addrName");
        String businessType = ctx.getVariable("businessType");
        String instId = ctx.getVariable("instId");
        String accountNumber = ctx.getAccountNumber();
        String downloadPath = Convert.appendPath(workPath.replace("\\\\", "\\"), "cache", addrName);
        if ("1001001".equals(businessType)) {
            downloadPath = Convert.appendPath(downloadPath, "社保", accountNumber);
        } else if ("1001002".equals(businessType)) {
            downloadPath = Convert.appendPath(downloadPath, "公积金", accountNumber);
        }
        FileUtil.mkdir(downloadPath);
        ctx.setVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH, downloadPath);
        ctx.setVariable(RobotConstant.DATA_PATH, downloadPath);

        //固定一个报盘存储文件路径
        String declareName = DeclareTypeEnum.getNameByCode(ctx.get("declareType"));
        if (StringUtils.isNotBlank(declareName)) {
            String tplTypeName = TplTypeCodeEnum.getNameByCode(ctx.get("tplTypeCode"));
            String fileSuffix = dict.getStr("fileSuffix");
            String filePath = Convert.appendPath(downloadPath, accountNumber + "_" + tplTypeName + "_" + declareName + "_" + instId + fileSuffix);
            log.info("下载报盘文件: filePath=" + filePath);
            ctx.setVariable(RobotConstant.FILE_PATH, filePath);
        }
        return downloadPath;
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

    private Dict checkDeclareData(Dict resultMap, RobotFlowStep flowStep, RobotFlowVO flow) {
        Map<String, Object> reqMap = Maps.newHashMap();
        reqMap.put("addrName", ctx.get("addrName"));
        reqMap.put("accountNumber", ctx.getAccountNumber());
        reqMap.put("tplTypeCode", ctx.get("tplTypeCode"));
        reqMap.put("businessType", Convert.getBusinessType(ctx.get("businessType")));
        reqMap.put("tbDate", DateUtil.date().toString("yyyy-MM"));
        String tplTypeCode = ctx.get("tplTypeCode");
        if ("10007007".equals(tplTypeCode)) {
            reqMap.put("operationType", "1016");
        }
        if (flowStep != null) {
            JSONObject jsonObject = JSON.parseObject(flowStep.getActionArgs());
            String businessNode = jsonObject.getString("businessNode");
            if (StringUtils.isNotBlank(businessNode)) {
                OfferLabelType offerLabelType = OfferLabelType.getOfferLabelType(businessNode);
                reqMap.put("operationType", offerLabelType.getCode());
            }
        }
        return checkOfferExportUrl(resultMap, reqMap, flowStep, flow);
    }

    private Dict checkOfferExportUrl(Dict resultMap, Map<String, Object> reqMap, RobotFlowStep flowStep, RobotFlowVO flow) {
        log.info(flow.getFlowName() + " 查询未申报/未核验数据: reqJson=" + JSON.toJSONString(reqMap));
        String resp = robotCommonService.checkOfferExportUrl(reqMap);
        log.info(flow.getFlowName() + " 查询未申报/未核验数据: respJson=" + resp);
        if (StringUtils.isBlank(resp)) {
            return resultMap;
        }
        JSONObject respJson = JSON.parseObject(resp);
        Integer num = 0;
        String fileSuffix = ".xlsx";//默认
        Integer declareType = ctx.get("declareType");
        if (declareType == null) {
            throw new RuntimeException("流程：flowCode=" + flowStep.getFlowCode() + ",未找到申报类型");
        }
        JSONObject jsonData = respJson.getJSONObject("data");
        if (declareType == 1) {//增员
            num = jsonData.getInteger("增员");
            fileSuffix = jsonData.getString("增员后缀");
        } else if (declareType == 2) {
            num = jsonData.getInteger("减员");
            fileSuffix = jsonData.getString("减员后缀");
        } else if (declareType == 3) {
            num = jsonData.getInteger("调基");
            fileSuffix = jsonData.getString("调基后缀");
        } else if (declareType == 5) {
            num = jsonData.getInteger("补缴");
            fileSuffix = jsonData.getString("补缴后缀");
        }
        resultMap.put("fileSuffix", fileSuffix);//申报文件后缀
        if (num == 0) {
            resultMap.put("isRunFlow", false);
        }
        return resultMap;
    }

    private RobotFlowVO getByFlowCode(String flowCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow flow = flowMapper.selectOneByExample(example);
        RobotFlowVO flowVO = new RobotFlowVO();
        BeanUtils.copyProperties(flow, flowVO);
        return flowVO;
    }
}
