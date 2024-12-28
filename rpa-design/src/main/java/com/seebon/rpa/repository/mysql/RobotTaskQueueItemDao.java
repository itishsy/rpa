package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotTaskQueueItem;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotTaskQueueItemDao extends Mapper<RobotTaskQueueItem>, MySqlMapper<RobotTaskQueueItem> {

}
