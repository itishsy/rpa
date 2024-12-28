package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotAppConfigForm;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/11/3 10:54
 * @Version 1.0
 **/
@Data
public class RobotAppFormVO extends RobotAppConfigForm {

    private String appCode;

    private Integer sort;

    private List<RobotAppGroupVO2> robotAppGroupVO2s;
}
