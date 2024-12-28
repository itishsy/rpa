package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotOfferRuleItemDao extends Mapper<RobotOfferRuleItem>, MySqlMapper<RobotOfferRuleItem> {

    void deleteByRuleId(@Param("ruleId") Integer ruleId);
}