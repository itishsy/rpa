package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotOfferRule;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotOfferRuleDao extends Mapper<RobotOfferRule>, MySqlMapper<RobotOfferRule> {

    List<RobotOfferRuleVO> selectByParams(Map<String,Object> params);

    int countByParams(Map<String,Object> params);
}