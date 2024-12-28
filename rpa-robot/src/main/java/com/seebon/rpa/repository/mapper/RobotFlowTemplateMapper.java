package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotFlowTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotFlowTemplateMapper extends Mapper<RobotFlowTemplate>, MySqlMapper<RobotFlowTemplate> {

    RobotFlowTemplate selectByFlowCode(@Param("flowCode") String flowCode);

    int clean(@Param("flowCode") String flowCode);
}
