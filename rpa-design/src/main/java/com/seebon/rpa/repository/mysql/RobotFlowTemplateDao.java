package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotFlowTemplate;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotFlowTemplateDao extends Mapper<RobotFlowTemplate>, MySqlMapper<RobotFlowTemplate> {

    List<RobotFlowTemplate> selectByAppCode(@Param("appCode") String appCode);

    List<RobotFlowTemplate> selectByTemplateFlowCode(@Param("templateFlowCode") String templateFlowCode);

    RobotFlowTemplate selectByFlowCode(@Param("flowCode") String flowCode);
}
