package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.RobotAppConfigCondition;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/10/28 19:32
 * @Version 1.0
 **/
@Data
public class RobotAppConfigConditionVO5 {
    private Integer showOrHidden;

    private String fieldName;

    private String relation;

    private List<RobotAppConfigCondition> robotAppConfigConditions;
}
