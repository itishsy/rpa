package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.RobotAppConfigCondition;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/10/31 11:19
 * @Version 1.0
 **/
@Data
public class RobotArgsDefineVO extends RobotArgsDefine {
    private List<RobotAppConfigCondition> conditionList;
}
