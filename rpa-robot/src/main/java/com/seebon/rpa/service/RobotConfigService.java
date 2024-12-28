package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotConfig;

import java.util.List;

public interface RobotConfigService {

    void saveConfig(List<RobotConfig> list);

    List<RobotConfig> listConfig();
}
