package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.RobotClientPriority;

import java.util.List;

public interface RobotClientPriorityService {

    List<RobotClientPriority> list();

    List<RobotClientApp> listApp();

    List<RobotClientPriority> list(String machineCode);

    void callbackPriority(String machineCode);

    int save(List<RobotClientPriority> priorities);
}
