package com.seebon.rpa.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.context.DebugContext;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.BusinessType;
import com.seebon.rpa.context.enums.OfferOperateType;
import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.RobotExecutionDetail;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.vo.DebugFlowStepVO;
import com.seebon.rpa.repository.mapper.RobotExecutionDetailMapper;
import com.seebon.rpa.repository.mapper.RobotExecutionMapper;
import com.seebon.rpa.runner.DebugExecutor;
import com.seebon.rpa.service.RobotDebugService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.enums.DeclareTypeEnum;
import com.seebon.rpa.utils.enums.TplTypeCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RobotDebugServiceImpl implements RobotDebugService {
    @Autowired
    private RobotExecutionMapper executionMapper;
    @Autowired
    private RobotExecutionDetailMapper detailMapper;
    @Autowired
    private DebugExecutor debugExecutor;
    @Autowired
    private RobotContext ctx;
    @Autowired
    private DriverTarget driverTarget;

    @Override
    public void debugFlowStep(DebugFlowStepVO flowStep) {
        List<RobotFlowStep> steps = flowStep.getFlowSteps();
        String flowCode = steps.get(0).getFlowCode();
        String executionCode = flowStep.getExecutionCode();
        List<String> debugStepCodes = flowStep.getDebugStepCodes();
        try {
            //初始化
            this.initCtxByTask(executionCode, flowCode, flowStep.getFlowArgs());

            //添加执行记录
            Integer executionId = this.addExecution(executionCode, flowCode);

            //校验申报账户
            WebDriver driver = ctx.getDriver();
            String driverKey = ctx.getDriverKey();
            if (driver != null && StringUtils.isBlank(driverKey)) {
                List<RobotExecutionDetail> details = this.getExecutionDetail(executionCode, "当前运行的户与上次不一致, 请关闭浏览器后重新运行.");
                detailMapper.insertList(details);
                return;
            }
            //文件存储路径
            this.mkDownloadFilePath(this.getTplTypeCode(steps));
            //设置断点编码
            ctx.setVariable(RobotConstant.DEBUG_STEP_CODES, debugStepCodes);
            //更新执行记录
            RobotExecution updateExecution = new RobotExecution();
            updateExecution.setId(executionId);
            updateExecution.setStatus(1);
            try {
                this.openBrowser(flowStep.getChromeArgs());
                //执行
                debugExecutor.start(steps);
            } catch (Exception e) {
                updateExecution.setStatus(0);
                if (e.getMessage() != null && e.getMessage().length()>2000) {
                    updateExecution.setError(e.getMessage().substring(0, 1999));
                }
            } finally {
                updateExecution.setEndTime(new Date());
                executionMapper.updateByPrimaryKeySelective(updateExecution);
            }
        } catch (Exception e) {
            log.error("本地调试执行异常." + e.getMessage(), e);
        } finally {
            //释放内存
            ctx.release();
        }
    }

    @Override
    public void addFlowStep(DebugFlowStepVO flowStep) {
        List<RobotFlowStep> steps = flowStep.getFlowSteps();
        String flowCode = steps.get(0).getFlowCode();
        String executionCode = flowStep.getExecutionCode();
        List<String> debugStepCodes = flowStep.getDebugStepCodes();
        try {
            //初始化
            this.initCtxByTask(executionCode, flowCode, flowStep.getFlowArgs());

            //添加执行记录
            Integer executionId = this.addExecution(executionCode, flowCode);

            //校验申报账户
            WebDriver driver = ctx.getDriver();
            String driverKey = ctx.getDriverKey();
            if (driver != null && StringUtils.isBlank(driverKey)) {
                List<RobotExecutionDetail> details = this.getExecutionDetail(executionCode, "当前运行的户与上次不一致, 请关闭浏览器后重新运行.");
                detailMapper.insertList(details);
                return;
            }
            //文件存储路径
            this.mkDownloadFilePath(this.getTplTypeCode(steps));
            //设置断点编码
            ctx.setVariable(RobotConstant.DEBUG_STEP_CODES, debugStepCodes);
            //更新执行记录
            RobotExecution updateExecution = new RobotExecution();
            updateExecution.setId(executionId);
            updateExecution.setStatus(1);
            try {
                //执行
                debugExecutor.start(steps);
            } catch (Exception e) {
                updateExecution.setStatus(0);
                if (e.getMessage() != null && e.getMessage().length()>2000) {
                    updateExecution.setError(e.getMessage().substring(0, 1999));
                }
            } finally {
                updateExecution.setEndTime(new Date());
                executionMapper.updateByPrimaryKeySelective(updateExecution);
            }
        } catch (Exception e) {
            log.error("本地调试执行异常." + e.getMessage(), e);
        } finally {
            //释放内存
            ctx.quit();
        }
    }

    @Override
    public void sendCommand(String command) {
        DebugContext.command = command;
        if ("continue".equals(command)) {
            synchronized (DebugContext.lock) {
                DebugContext.lock.release();
            }
        }
        //(上一步(back)、下一步(next)、暂停(suspend)、继续(continue)、停止(stop))
    }

    private void openBrowser(Map<String, Object> optionMap) {
        String driverPath = (String) optionMap.get("driverPath");
        driverTarget.setDriverPath(driverPath);
        if (optionMap.get("pluginPath") != null) {
            driverTarget.setPlugsPath((String) optionMap.get("pluginPath"));
        }
        if (optionMap.get("sourcePath") != null) {
            driverTarget.setBinaryPath((String) optionMap.get("sourcePath"));
        }
        driverTarget.setOptionMap(Maps.newHashMap());
        driverTarget.fetchTarget();
    }

    private Dict getTplTypeCode(List<RobotFlowStep> steps) {
        Dict resultMap = Dict.create();
        for (RobotFlowStep step : steps) {
            if (!"httpDownload".equals(step.getActionCode())) {
                continue;
            }
            JSONObject targetArgMap = JSON.parseObject(step.getTargetArgs());
            JSONObject params = ELParser.parse(targetArgMap.getString("params"), ctx.getVariables(), JSONObject.class);
            String method = params.getString("method");
            if (StringUtils.isBlank(method) || !"postDeclare".equals(method)) {
                continue;
            }
            resultMap.put("declareType", params.getInteger("declareType"));
            resultMap.put("tplTypeCode", params.getString("tplTypeCode"));
        }
        if (MapUtils.isNotEmpty(resultMap)) {
            return resultMap;
        }
        for (RobotFlowStep step : steps) {
            if (!"pullOffer".equals(step.getActionCode())) {
                continue;
            }
            JSONObject actionArgsMap = JSON.parseObject(step.getActionArgs());
            BusinessType businessType = BusinessType.getBusinessType(actionArgsMap.getString("businessType"));
            if ("备案系统".equals(businessType.getName())) {
                return resultMap;
            }
            OfferOperateType operateType = OfferOperateType.getOfferOperateType(actionArgsMap.getString("operateType"));
            OfferOperateType accfundOperateType = OfferOperateType.getOfferOperateType(actionArgsMap.getString("accfundOperateType"));
            if (businessType.getType() == 1) {
                resultMap.put("declareType", operateType.getType());
            } else {
                resultMap.put("declareType", accfundOperateType.getType());
            }
            resultMap.put("tplTypeCode", businessType.getTplCode());
        }
        return resultMap;
    }

    private void initCtxByTask(String executionCode, String flowCode, Map<String, Object> flowArgs) {
        ctx.init();
        //执行批次
        String uuid = UUIDGenerator.uuidStringWithoutLine();
        ctx.setVariable(RobotConstant.BATCH_CODE, uuid);
        ctx.setVariable(RobotConstant.INST_ID, uuid);
        //执行码
        ctx.setVariable(RobotConstant.EXECUTION_CODE_KEY, executionCode);
        ctx.setVariable("flowCode", flowCode);
        for (String key : flowArgs.keySet()) {
            ctx.setVariable(key, flowArgs.get(key));
        }
    }

    private Integer addExecution(String executionCode, String flowCode) {
        RobotExecution addExecution = new RobotExecution();
        addExecution.setExecutionCode(executionCode);
        addExecution.setClientId(ctx.get("clientId"));
        addExecution.setMachineCode(ctx.get("machineCode"));
        addExecution.setTaskCode(ctx.get("taskCode"));
        addExecution.setFlowCode(flowCode);
        addExecution.setStartTime(new Date());
        addExecution.setStatus(2);
        executionMapper.insertSelective(addExecution);
        return addExecution.getId();
    }

    private List<RobotExecutionDetail> getExecutionDetail(String executionCode, String msg) {
        String flowCode = ctx.get("flowCode");
        String flowName = ctx.get("flowName");
        RobotExecutionDetail detail = new RobotExecutionDetail();
        detail.setExecutionCode(executionCode);
        detail.setFlowCode(flowCode);
        detail.setStepName("流程：" + flowName + " 错误：" + msg);
        detail.setStartTime(new Date());
        detail.setStatus(0);
        detail.setEndTime(new Date());
        detail.setError("流程：" + flowName + " 错误：" + msg);
        log.info("流程：" + flowName + " 错误：" + msg);
        return Lists.newArrayList(detail);
    }

    private String mkDownloadFilePath(Dict dict) {
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
}
