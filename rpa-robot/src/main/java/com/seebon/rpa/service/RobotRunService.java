package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotExecutionDetail;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.vo.DebugFlowStepVO;

import java.util.List;

public interface RobotRunService {

    /**
     * 启动任务的流程。本地调试调用
     *
     * @return
     */
    List<RobotExecutionDetail> taskFlow(String taskCode, String flowCode);

    /**
     * 启动任务的流程。本地调试调用
     *
     * @return
     */
    List<RobotExecutionDetail> startFlow(String taskCode, String flowCode);

    /**
     * 启动任务的流程。本地调试调用
     *
     * @return
     */
    List<RobotExecutionDetail> startFlowStep(String taskCode, String flowCode, String stepCode);

    /**
     * 启动任务的流程。远程调试调用
     *
     * @return
     */
    void startTestFlowStep(String taskCode, String flowCode, String executionCode, List<RobotFlowStep> steps);
}
