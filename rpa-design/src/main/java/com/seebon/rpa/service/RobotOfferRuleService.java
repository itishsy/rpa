package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;

import java.util.List;
import java.util.Map;

public interface RobotOfferRuleService {

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    IgGridDefaultPage<RobotOfferRuleVO> page(Map<String, Object> params);

    /**
     * 查询列表
     *
     * @param ruleVO
     * @return
     */
    List<RobotOfferRuleVO> list(RobotOfferRuleVO ruleVO);

    /**
     * 查询列表
     *
     * @param ruleId
     * @return
     */
    RobotOfferRuleVO getByRuleId(Integer ruleId);

    /**
     * 保存
     *
     * @param robotOfferRuleVO
     */
    void save(RobotOfferRuleVO robotOfferRuleVO);

    /**
     * 删除
     *
     * @param itemId
     */
    void deleteById(Integer itemId);
}
