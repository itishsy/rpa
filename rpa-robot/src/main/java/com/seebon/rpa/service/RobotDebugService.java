package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.vo.DebugFlowStepVO;

public interface RobotDebugService {
    /**
     * 启动任务的流程。本地调试调用
     *
     * @return
     */
    void debugFlowStep(DebugFlowStepVO flowStep);

    /**
     * 启动任务的流程。本地调试调用
     *
     * @return
     */
    void addFlowStep(DebugFlowStepVO flowStep);

    /**
     * 启动任务的流程。本地调试调用
     *
     * @return
     */
    void sendCommand(String command);
}
