package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotFlowStepArgs;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotFlowStepArgsMapper extends Mapper<RobotFlowStepArgs>, MySqlMapper<RobotFlowStepArgs> {

    /**
     * 根据flowCode查询RobotFlowStep
     *
     * @param flowCode 流程编码
     * @return flowStep
     */
    RobotFlowStepArgs selectByFlowCode(@Param("flowCode") String flowCode, @Param("groupCode") String groupCode, @Param("stepCode") String stepCode);

    /**
     * 根据flowCode查询RobotFlowStepArgs
     *
     * @return flowStep
     */
    List<RobotFlowStepArgs> selectAllByFlowCode(@Param("flowCode") String flowCode);

    int deleteByFlowCode(@Param("flowCode") String flowCode);
}
