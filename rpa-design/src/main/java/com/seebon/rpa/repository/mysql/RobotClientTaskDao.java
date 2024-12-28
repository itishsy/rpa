package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotClientTask;
import tk.mybatis.mapper.common.Mapper;

public interface RobotClientTaskDao extends Mapper<RobotClientTask> {

    int cleanSyncTime(String taskCode);
}
