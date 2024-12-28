package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotAppCa;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;

import java.util.List;

public interface RobotAppCaService {

    void installAppCa(RobotTaskQueueVO queueVO);

    void unInstallAppCa(RobotTaskQueueVO queueVO);

    void unInstallAppCa();

    RobotAppCa getRobotAppCa(String appCode, String declareSystem, String declareAccount);

    void saveAppCa(List<RobotAppCaVO> list);
}
