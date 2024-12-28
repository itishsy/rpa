package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.Robot;
import com.seebon.rpa.entity.robot.RobotApp;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/8/25 10:33
 * @Version 1.0
 **/
@Data
public class RobotVO extends Robot {
    private List<RobotApp> robotApps;

    private List<RobotClientAppVO> robotClientAppVOS;
}
