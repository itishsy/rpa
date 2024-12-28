package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotClientPriority;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotClientPriorityMapper extends Mapper<RobotClientPriority>, MySqlMapper<RobotClientPriority> {

    void clean();

    RobotClientPriority selectByTaskCode(@Param("taskCode") String taskCode);
}
