package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.RobotAppConfigForm;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/10/26 15:20
 * @Version 1.0
 **/
@Data
public class RobotAppFormVO extends RobotAppConfigForm {

    private Integer sort;

    private List<RobotAppGroupVO> robotAppGroupVOS;
}
