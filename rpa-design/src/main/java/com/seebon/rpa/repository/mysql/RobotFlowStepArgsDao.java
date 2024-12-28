package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotFlowStepArgs;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotFlowStepArgsDao extends Mapper<RobotFlowStepArgs>, MySqlMapper<RobotFlowStepArgs> {


    /**
     * 根据flowCode查询RobotFlowStep
     *
     * @param appCode 流程编码
     * @return flowStep
     */
    List<RobotFlowStepArgs> selectByAppCode(@Param("appCode") String appCode);

    /**
     * 根据flowCode查询RobotFlowStep
     *
     * @param flowCode 流程编码
     * @return flowStep
     */
    RobotFlowStepArgs selectByFlowCode(@Param("flowCode") String flowCode, @Param("groupCode") String groupCode, @Param("stepCode") String stepCode);

    /**
     * 根据flowCode删除
     *
     * @param flowCode 流程编码
     * @return flowStep
     */
    void deleteByFlowCode(@Param("flowCode") String flowCode);


    /**
     * 根据flowCode删除
     *
     * @param flowCode 流程编码
     * @return flowStep
     */
    void deleteByFlowCodeAndGroupCode(@Param("flowCode") String flowCode, @Param("groupCode") String groupCode);
}
