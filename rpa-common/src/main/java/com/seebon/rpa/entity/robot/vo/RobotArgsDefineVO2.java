package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotAppConfigCondition;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/11/9 13:52
 * @Version 1.0
 **/
@Data
public class RobotArgsDefineVO2 extends RobotArgsDefine {
    private List<RobotAppConfigCondition> configConditions;
}
