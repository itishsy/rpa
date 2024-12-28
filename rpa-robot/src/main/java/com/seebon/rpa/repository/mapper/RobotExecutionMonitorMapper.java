package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotExecutionMonitor;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RobotExecutionMonitorMapper extends Mapper<RobotExecutionMonitor> {

    RobotExecutionMonitor selectByExecutionCode(String executionCode);

    List<RobotExecutionMonitor> selectUnSyncMonitor();
}
