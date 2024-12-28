package com.seebon.rpa;

import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.runner.RobotRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ActionBaseTests {

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected RobotRunner robotRunner;

    @Autowired
    protected RobotContext ctx;

    private List<RobotFlowStep> steps = new ArrayList<>();

    @BeforeEach
    void contextLoads() {
        SpringBeanHolder.setApplicationContext(applicationContext);
    }

    protected void addStep(String stepName, String actionCode, String actionArgs) {
        steps.add(newStep(stepName, actionCode, actionArgs, null, null));
    }

    protected void addStep(String stepName, String actionCode, String actionArgs, String targetArgs) {
        steps.add(newStep(stepName, actionCode, actionArgs, targetArgs, null));
    }

    protected void addStep(String stepName, String actionCode, String actionArgs, String targetArgs, String skipTo) {
        steps.add(newStep(stepName, actionCode, actionArgs, targetArgs, skipTo));
    }

    private RobotFlowStep newStep(String stepName, String actionCode, String actionArgs, String targetArgs, String skipTo) {
        RobotFlowStep step = new RobotFlowStep();
        step.setStepName(stepName);
        step.setActionCode(actionCode);
        step.setActionArgs(actionArgs);
        step.setTargetArgs(targetArgs);
        step.setSkipTo(skipTo);
        step.setStatus(1);
        return step;
    }

    @AfterEach
    void runnerStart() {
        /*robotRunner.start(steps);*/
    }
}
