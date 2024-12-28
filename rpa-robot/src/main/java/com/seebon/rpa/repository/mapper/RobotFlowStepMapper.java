package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotFlowStep;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotFlowStepMapper extends Mapper<RobotFlowStep>, MySqlMapper<RobotFlowStep> {

    /**
     * 根据flowCode查询flowStep
     *
     * @param flowCode 流程编码
     * @return flowStep
     */
    List<RobotFlowStep> findSteps(@Param("flowCode") String flowCode);

    /**
     * 根据groupCode查询flowStep
     *
     * @param groupCode 流程编码
     * @return flowStep
     */
    List<RobotFlowStep> listByGroupCode(@Param("flowCode") String flowCode, @Param("groupCode") String groupCode);

    /**
     * 根据flowCode查询flowStep
     */
    List<RobotFlowStep> findSubSteps(@Param("flowCode") String flowCode, @Param("flowName") String flowName);

    /**
     * 根据flowCode查询flowStep
     */
    String findSubRelationFlowCode(@Param("flowCode") String flowCode, @Param("flowName") String flowName);

    /**
     * 根据flowCode查询flowStep
     */
    List<RobotFlowStep> findTemplateSteps(@Param("flowName") String flowName);

    /**
     * 根据flowCode查询flowStep
     */
    RobotFlowStep getByTargetArgs(@Param("flowCode") String flowCode, @Param("actionCode") String actionCode,
                                  @Param("targetArgs") String targetArgs);

    /**
     * 根据flowCode查询flowStep
     */
    RobotFlowStep getByActionArgs(@Param("flowCode") String flowCode, @Param("actionCode") String actionCode,
                                  @Param("actionArgs") String actionArgs);
}
