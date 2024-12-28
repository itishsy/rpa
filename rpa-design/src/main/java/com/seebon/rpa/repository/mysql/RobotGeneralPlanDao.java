package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotGeneralPlan;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotGeneralPlanDao extends Mapper<RobotGeneralPlan>, MySqlMapper<RobotGeneralPlan> {
}
