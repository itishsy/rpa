package com.seebon.rpa.service;


import com.seebon.rpa.entity.robot.dto.design.MonitorCountVo;
import com.seebon.rpa.entity.robot.dto.design.TaskCountVo;

import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe 监控台接口
 * @date 2023-07-24 16:26
 */
public interface MonitorStationService{

    MonitorCountVo getMonitorCount();

    Map getCity();

    TaskCountVo getTaskCount();
}
