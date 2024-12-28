package com.seebon.rpa.actions;

import com.seebon.rpa.entity.robot.RobotFlowStep;

import java.util.Map;

public interface Action extends Runnable {

    /**
     * 初始化
     */
    void init(RobotFlowStep step);

    /**
     * 执行操作
     */
    void run();

    /**
     * 添加变量
     *
     * @return
     */
    void variable(String key, Object object);

    /**
     * 所有变量
     *
     * @return
     */
    Map<String, Object> getVariables();

    /**
     * 执行超时时间
     *
     * @return
     */
    int getTimeout();

    /**
     * 操作目标
     *
     * @return
     */
    <T> T getTarget();

    /**
     * 释放
     */
    void release();
}
