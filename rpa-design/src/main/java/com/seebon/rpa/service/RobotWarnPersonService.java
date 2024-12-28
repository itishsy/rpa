package com.seebon.rpa.service;


import com.seebon.rpa.entity.robot.po.design.RobotWarnPersonType;

import java.util.List;

public interface RobotWarnPersonService{


    /**
     * 获取人员信息分类
     * @return
     */
    List<RobotWarnPersonType> getPersonType();
}
