package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotExecutionData;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotExecutionDataDao extends Mapper<RobotExecutionData>, MySqlMapper<RobotExecutionData> {

}
