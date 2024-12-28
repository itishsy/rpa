package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;

import java.util.List;

public interface RobotExecutionService {

    /**
     * 添加执行记录
     */
    Integer addExecution(RobotTaskQueueVO queueVO, String flowCode);

    /**
     * 同步执行记录
     */
    Integer syncExecution();


    @Deprecated
    Integer syncExecutionData();

    /**
     * 获取执行记录
     */
    List<RobotExecution> getByExecutionCode(String executionCode);
}
