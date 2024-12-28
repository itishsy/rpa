package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;

import java.util.List;

public interface RobotTaskSessionKeepService {

    Integer getSessionKeepPort();

    RobotTaskSessionKeep getSessionKeep();

    void saveSessionKeep(List<RobotTaskSessionKeep> list);

    void syncSessionKeep(Integer loginStatus, String comment);
}
