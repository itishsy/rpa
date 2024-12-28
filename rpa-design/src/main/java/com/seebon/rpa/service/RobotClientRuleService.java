package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotClientRuleCondition;
import com.seebon.rpa.entity.robot.vo.RobotClientRuleVO;

import java.util.List;
import java.util.Map;

public interface RobotClientRuleService {

    IgGridDefaultPage<RobotClientRuleVO> page(Map<String, Object> map);

    Integer enableOrStop(Integer id, Integer status);

    Integer save(RobotClientRuleVO ruleVO);

    RobotClientRuleVO getById(Integer id);

    Integer updateSort(Integer id, Integer sort);

    Map<Integer, List<RobotClientRuleCondition>> getRuleConditionList(List<Integer> ruleIds);
}
