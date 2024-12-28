package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotArgsDefine;
import lombok.Data;

/**
 * @Author hao
 * @Date 2022/8/26 17:31
 * @Version 1.0
 **/
@Data
public class RobotArgsDefineVO extends RobotArgsDefine {
    private String groupName;

    private String selectedType;
}
