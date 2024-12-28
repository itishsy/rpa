package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.dto.design.*;

import java.util.List;

public interface RobotFlowDesignService {
    /**
     * 保存流程图。
     * hsy：简化流程图，子流程步骤不再引用模板。 因此，不再接收stepCode
     * @param flowDesign
     * @return
     */
    String add(RobotFlowDesignVO flowDesign);

    /**
     * 流程设计-获取
     *
     * @param exVO
     * @return
     */
    RobotFlowOpenTypeVO getFlowOpenType(String appCode, String flowCode, String stepCode);

    /**
     * 流程设计-获取
     *
     * @param exVO
     * @return
     */
    RobotFlowDesign getByCode(RobotFlowDesignExVO exVO);

    /**
     * 流程设计-复制
     *
     * @param exVO
     * @return
     */
    RobotFlowDesign copyByCode(RobotFlowDesignCopyVO exVO);

    /**
     * 流程设计-获取子流程
     *
     * @param appCode
     * @return
     */
    List<RobotFlow> getSubByAppCode(String appCode);

    /**
     * 同步入
     *
     * @param syncVO
     * @return
     */
    int syncIn(RobotFlowDesignSyncVO syncVO);

    /**
     * 同步出
     *
     * @param comment
     * @return
     */
    int syncOut(String comment);
}
