package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotExecutionMonitor;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotExecutionMonitorDao extends Mapper<RobotExecutionMonitor>, MySqlMapper<RobotExecutionMonitor> {

    List<RobotExecutionMonitor> selectByTaskCode(@Param("taskCode") String taskCode, @Param("machineCode") String machineCode);
}
