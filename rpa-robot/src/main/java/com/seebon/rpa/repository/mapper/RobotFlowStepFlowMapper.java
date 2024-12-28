package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotFlowStepFlow;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotFlowStepFlowMapper extends Mapper<RobotFlowStepFlow>, MySqlMapper<RobotFlowStepFlow> {

    /**
     * 根据 flowCode 查询 RobotFlowStepFlow
     *
     * @param flowCode 流程编码
     * @return flowStep
     */
    RobotFlowStepFlow selectByFlowCode(@Param("flowCode") String flowCode, @Param("stepCode") String stepCode);

    /**
     * 根据 stepCode 查询 RobotFlowStepFlow
     *
     * @param stepCode 流程编码
     * @return flowStep
     */
    RobotFlowStepFlow selectByStepCode(@Param("stepCode") String stepCode);
}
