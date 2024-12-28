package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotClientRule;
import com.seebon.rpa.entity.robot.RobotClientRuleCondition;
import lombok.Data;

import java.util.List;

@Data
public class RobotClientRuleVO extends RobotClientRule {

    private String customerName;

    private String conclusion;

    private List<RobotClientRuleCondition> ruleConditionList;
}
