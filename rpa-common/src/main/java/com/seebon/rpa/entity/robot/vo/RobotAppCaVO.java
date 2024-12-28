package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotAppCa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RobotAppCaVO extends RobotAppCa {
    private String declareSystemName;
    private String appName;
}