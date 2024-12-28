package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotFlowStatusHistory;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotFlowStatusHistoryDao extends Mapper<RobotFlowStatusHistory>, MySqlMapper<RobotFlowStatusHistory> {
}