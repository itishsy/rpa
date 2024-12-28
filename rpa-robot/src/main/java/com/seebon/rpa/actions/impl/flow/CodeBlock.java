package com.seebon.rpa.actions.impl.flow;

import com.seebon.rpa.actions.ActionFactory;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.ScreenShotUtil;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.RobotFlowStepArgs;
import com.seebon.rpa.repository.mapper.RobotExecutionDetailMapper;
import com.seebon.rpa.repository.mapper.RobotFlowStepArgsMapper;
import com.seebon.rpa.repository.mapper.RobotFlowStepMapper;
import com.seebon.rpa.runner.RobotExecutor;
import com.seebon.rpa.service.RobotExecutionFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@RobotAction(name = "代码块", order = 30, comment = "开始")
public class CodeBlock extends AbstractAction {

    @ActionArgs(value = "引用代码块名称")
    private String codeBlockName;

    @Autowired
    private RobotFlowStepMapper flowStepMapper;
    @Autowired
    private RobotFlowStepArgsMapper flowStepArgsMapper;
    @Autowired
    private ActionFactory actionFactory;
    @Autowired
    private RobotExecutionDetailMapper detailMapper;
    @Autowired
    private RobotExecutionFileService robotExecutionFileService;
    @Autowired
    private ScreenShotUtil screenShotUtil;

    @Override
    public void run() {
        //地区-主流程-流程编码
        String flowCodeMain = ctx.getVariable("flowCode");
        //模板流程编码
        String templateFlowCode = ctx.getVariable("templateFlowCode");
        System.out.println("flowCode=" + flowCode + ",stepCode=" + stepCode + ",flowCodeMain=" + flowCodeMain + ",templateFlowCode=" + templateFlowCode);

        //代码块步骤
        List<RobotFlowStep> steps = flowStepMapper.listByGroupCode(flowCode, stepCode);
        if((null == steps || steps.size() == 0) && ctx.contains("subFlowCode")){
            steps = flowStepMapper.listByGroupCode(ctx.getVariable("subFlowCode"), stepCode);
        }
        for (RobotFlowStep step : steps) {
            RobotFlowStepArgs stepArgs = flowStepArgsMapper.selectByFlowCode(flowCodeMain, stepCode, step.getStepCode());
            if (stepArgs != null) {
                step.setActionArgs(stepArgs.getActionArgs());
                step.setTargetArgs(stepArgs.getTargetArgs());
            }
        }
        RobotExecutor robotExecutor = new RobotExecutor();
        robotExecutor.setSubFlow(true);
        robotExecutor.setCtx(ctx);
        robotExecutor.setActionFactory(actionFactory);
        robotExecutor.setDetailMapper(detailMapper);
        robotExecutor.setRobotExecutionFileService(robotExecutionFileService);
        robotExecutor.setScreenShotUtil(screenShotUtil);
        robotExecutor.start(steps);
    }
}
