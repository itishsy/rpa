package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.dto.RobotTaskDTO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;

import java.util.Date;

public interface RobotExecutionMonitorService {

    /**
     * 添加监控
     *
     * @param task
     */
    void addMonitor(RobotTaskQueueVO task, Date startTime);

    /**
     * 同步监控数据
     */
    Integer syncMonitorData();
}
