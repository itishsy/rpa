package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAccountStatusHistory;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotAccountStatusHistoryDao extends Mapper<RobotAccountStatusHistory>, MySqlMapper<RobotAccountStatusHistory> {
}