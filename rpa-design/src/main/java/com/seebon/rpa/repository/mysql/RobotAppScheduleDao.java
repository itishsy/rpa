package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAppSchedule;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotAppScheduleDao extends Mapper<RobotAppSchedule>,MySqlMapper<RobotAppSchedule> {
    Integer updateByExecOrder(@Param("execOrder") Integer execOrder, @Param("flowCodes")List<String> flowCodes);
}
