package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotAppCa;
import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import com.seebon.rpa.entity.saas.po.system.SysDataDict;

import java.util.List;
import java.util.Map;

public interface RobotAppCaService {

    IgGridDefaultPage<RobotAppCaVO> list(Map<String, Object> map);

    Integer save(RobotAppCa appCa);

    RobotAppCaVO getById(Integer id);

    void againSync(Integer id);

    void disabled(RobotAppCa appCa);
}
