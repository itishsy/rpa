package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotExecutionData;
import com.seebon.rpa.entity.robot.RobotExecutionMonitor;
import com.seebon.rpa.entity.robot.RobotExecutionVoucher;
import com.seebon.rpa.entity.robot.dto.RobotExecutionDTO;

import java.util.List;

public interface RobotExecutionService {

    /**
     * 添加执行记录
     */
    int addExecution(RobotExecutionDTO executionDTO);

    /**
     * 添加执行监控
     */
    int addMonitor(List<RobotExecutionMonitor> monitorList);

    /**
     * 保存执行数据
     */
    int saveExecutionData(List<RobotExecutionData> executionDataList);

    /**
     * 保存执行凭证
     */
    int saveExecutionVoucher(RobotExecutionVoucher voucher);
}
