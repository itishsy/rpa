package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotClientRule;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotClientRuleDao extends Mapper<RobotClientRule>, MySqlMapper<RobotClientRule> {

    void batchUpdateSort(List<RobotClientRule> list);
}
