package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotExecutionDetail;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotExecutionDetailMapper extends Mapper<RobotExecutionDetail>, MySqlMapper<RobotExecutionDetail>, IdsMapper<RobotExecutionDetail> {

}
