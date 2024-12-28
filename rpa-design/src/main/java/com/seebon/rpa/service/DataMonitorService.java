package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.dto.monitor.TodayExecDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecQueryDTO;

public interface DataMonitorService {

    TodayExecDTO getTodayExecList(TodayExecQueryDTO dto);

}
