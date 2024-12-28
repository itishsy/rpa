package com.seebon.rpa.actions.impl.flow;

import com.google.common.collect.Maps;
import com.seebon.rpa.actions.ActionFactory;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.ScreenShotUtil;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.repository.mapper.*;
import com.seebon.rpa.runner.FlowExecutor;
import com.seebon.rpa.runner.RobotExecutor;
import com.seebon.rpa.runner.SyncOutService;
import com.seebon.rpa.service.RobotExecutionFileService;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "调用流程", order = 10, targetType = NoneTarget.class, comment = "执行调用流程")
public class SubFlow extends AbstractAction {

    @ActionArgs(value = "流程名称", required = true)
    private String flowName;

    @Autowired
    private RobotFlowStepMapper flowStepMapper;
    @Autowired
    private RobotFlowMapper flowMapper;
    @Autowired
    private RobotFlowTemplateMapper flowTemplateMapper;
    @Autowired
    private ActionFactory actionFactory;
    @Autowired
    private RobotExecutionDetailMapper detailMapper;
    @Autowired
    private RobotExecutionFileService robotExecutionFileService;
    @Autowired
    private SyncOutService syncOutService;

    @Autowired
    private RobotFlowStepArgsMapper flowStepArgsMapper;
    @Autowired
    private RobotExecutionMapper executionMapper;
    @Autowired
    private ScreenShotUtil screenShotUtil;

    @Override
    public void run() {
        log.info("执行子流程：" + flowName + " flowCode." + flowCode);
        List<RobotFlowStep> steps = this.getFlowSteps();
        if (CollectionUtils.isEmpty(steps)) {
            throw new RobotConfigException("无操作步骤.");
        }
        //添加执行记录
        Integer executionId = this.addExecution(steps.get(0).getFlowCode());
        //更新执行记录
        RobotExecution updateExecution = new RobotExecution();
        updateExecution.setId(executionId);
        updateExecution.setStatus(1);
        try {
            if (Convert.isContains(steps)) {
                //流程执行器
                this.runFlowExecutor(steps);
            } else {
                //步骤执行器
                this.runRobotExecutor(steps);
            }
        } catch (Exception e) {
            log.error("子流程：" + flowName + " 执行异常." + e.getMessage(), e);
            updateExecution.setStatus(0);
            if (e.getMessage() != null && e.getMessage().length()>2000) {
                updateExecution.setError(e.getMessage().substring(0, 1999));
            }
            String tagCode = this.getTagCode(steps.get(0).getFlowCode());
            syncOutService.loginSuccess(tagCode, 2, "登录失败");
            throw e;
        } finally {
            updateExecution.setEndTime(new Date());
            executionMapper.updateByPrimaryKeySelective(updateExecution);
        }
    }

    private void runFlowExecutor(List<RobotFlowStep> steps) {
        FlowExecutor flowExecutor = new FlowExecutor();
        flowExecutor.setSubFlow(true);
        flowExecutor.setCtx(ctx);
        flowExecutor.setActionFactory(actionFactory);
        flowExecutor.setDetailMapper(detailMapper);
        flowExecutor.setRobotExecutionFileService(robotExecutionFileService);
        flowExecutor.setScreenShotUtil(screenShotUtil);
        flowExecutor.start(steps);
    }

    private void runRobotExecutor(List<RobotFlowStep> steps) {
        RobotExecutor robotExecutor = new RobotExecutor();
        robotExecutor.setSubFlow(true);
        robotExecutor.setCtx(ctx);
        robotExecutor.setActionFactory(actionFactory);
        robotExecutor.setDetailMapper(detailMapper);
        robotExecutor.setRobotExecutionFileService(robotExecutionFileService);
        robotExecutor.setScreenShotUtil(screenShotUtil);
        robotExecutor.start(steps);
    }

    private List<RobotFlowStep> getFlowSteps() {
        String templateFlowCode = ctx.getVariable("templateFlowCode");
        String mainFlowCode = ctx.getVariable("flowCode");
        if (StringUtils.isBlank(templateFlowCode)) {
            mainFlowCode = flowCode;
        }
        List<RobotFlowStep> steps = flowStepMapper.findSubSteps(this.getRelationFlowCode(mainFlowCode), flowName);
        if (CollectionUtils.isNotEmpty(steps)) {
            return steps;
        }
        String subRelationFlowCode = flowStepMapper.findSubRelationFlowCode(this.getRelationFlowCode(mainFlowCode), flowName);
        if (StringUtils.isNotBlank(subRelationFlowCode)) {
            steps = flowStepMapper.findSteps(subRelationFlowCode);
            if (CollectionUtils.isNotEmpty(steps)) {
                return steps;
            }
        }
        //子流程无步骤，可能引用了模板
        Example e = new Example(RobotFlow.class);
        e.createCriteria().andEqualTo("appCode", ctx.get("appCode")).andEqualTo("flowName", flowName);
        RobotFlow subFlow = flowMapper.selectOneByExample(e);
        RobotFlowTemplate flowTemplate = flowTemplateMapper.selectByFlowCode(subFlow.getFlowCode());
        if (flowTemplate != null) {
            ctx.setVariable("subFlowCode", subFlow.getFlowCode());
            steps = flowStepMapper.findSteps(flowTemplate.getTemplateFlowCode());
            List<RobotFlowStepArgs> stepArgs = flowStepArgsMapper.selectAllByFlowCode(subFlow.getFlowCode());
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
        } else {
            steps = flowStepMapper.findTemplateSteps(flowName);
        }
        return steps;
    }

    private Integer addExecution(String flowCode) {
        String executionCode = ctx.getVariable(RobotConstant.EXECUTION_CODE_KEY);
        String taskCode = ctx.getVariable("taskCode");
        Integer clientId = ctx.getVariable("clientId");

        RobotExecution addExecution = new RobotExecution();
        addExecution.setExecutionCode(executionCode);
        addExecution.setClientId(clientId);
        addExecution.setTaskCode(taskCode);
        addExecution.setFlowCode(flowCode);
        addExecution.setStartTime(new Date());
        addExecution.setStatus(2);
        executionMapper.insertSelective(addExecution);
        return addExecution.getId();
    }

    private String getTagCode(String flowCode) {
        log.info("流程编码：flowCode=" + flowCode);
        if (StringUtils.isBlank(flowCode)) {
            return null;
        }
        Example e = new Example(RobotFlow.class);
        e.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow flow = flowMapper.selectOneByExample(e);
        if (flow == null) {
            return null;
        }
        return flow.getTagCode();
    }

    private String getRelationFlowCode(String flowCode) {
        Example example = new Example(RobotFlow.class);
        example.createCriteria().andEqualTo("flowCode", flowCode);
        RobotFlow flow = flowMapper.selectOneByExample(example);
        if (flow == null || StringUtils.isBlank(flow.getRelationFlowCode())) {
            return flowCode;
        }
        return flow.getRelationFlowCode();
    }
}

