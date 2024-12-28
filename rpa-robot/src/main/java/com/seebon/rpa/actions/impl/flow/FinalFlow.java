package com.seebon.rpa.actions.impl.flow;

import com.seebon.rpa.actions.ActionFactory;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.ScreenShotUtil;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.repository.mapper.RobotExecutionDetailMapper;
import com.seebon.rpa.repository.mapper.RobotFlowMapper;
import com.seebon.rpa.repository.mapper.RobotFlowStepMapper;
import com.seebon.rpa.runner.RobotExecutor;
import com.seebon.rpa.service.RobotExecutionFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@RobotAction(name = "最后子流程",
        order = 50,
        targetType = NoneTarget.class,
        comment = "最后执行的子流程")
public class FinalFlow extends AbstractAction {

    @ActionArgs(value = "流程名称", required = true)
    private String flowName;

    @Autowired
    private RobotFlowStepMapper flowStepMapper;
    @Autowired
    private RobotExecutionDetailMapper detailMapper;
    @Autowired
    private RobotFlowMapper flowMapper;
    @Autowired
    private RobotExecutionFileService robotExecutionFileService;
    @Autowired
    private ScreenShotUtil screenShotUtil;
    @Autowired
    private ActionFactory actionFactory;

    @Override
    public void run() {
        List<RobotFlowStep> steps = flowStepMapper.findSubSteps(this.getRelationFlowCode(flowCode), flowName);
        RobotExecutor robotExecutor = new RobotExecutor();
        robotExecutor.setSubFlow(true);
        robotExecutor.setFinalFlow(true);
        robotExecutor.setCtx(ctx);
        robotExecutor.setActionFactory(actionFactory);
        robotExecutor.setDetailMapper(detailMapper);
        robotExecutor.setRobotExecutionFileService(robotExecutionFileService);
        robotExecutor.setScreenShotUtil(screenShotUtil);
        robotExecutor.start(steps);
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
