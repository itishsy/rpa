package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotAppConfigGroup;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/11/9 17:15
 * @Version 1.0
 **/
@Data
public class RobotAppGroupVO2 extends RobotAppConfigGroup {

    private Integer sort;

    private List<RobotArgsDefineVO2> robotArgsDefineVO2s;
}
