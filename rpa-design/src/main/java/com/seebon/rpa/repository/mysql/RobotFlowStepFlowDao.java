package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotFlowStepFlow;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotFlowStepFlowDao extends Mapper<RobotFlowStepFlow>, MySqlMapper<RobotFlowStepFlow> {

    /**
     * 根据flowCode查询RobotFlowStep
     *
     * @param flowCode 流程编码
     * @return flowStep
     */
    RobotFlowStepFlow selectByStepCode(@Param("flowCode") String flowCode, @Param("stepCode") String stepCode);

    /**
     * 根据flowCode查询RobotFlowStep
     *
     * @param flowCodes 流程编码
     */
    List<RobotFlowStepFlow> selectByFlowCodes(@Param("flowCodes") List<String> flowCodes);
}
