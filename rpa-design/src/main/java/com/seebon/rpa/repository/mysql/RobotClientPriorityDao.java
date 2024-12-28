package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotClientPriority;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotClientPriorityDao extends Mapper<RobotClientPriority>, MySqlMapper<RobotClientPriority> {

    void deleteByClientId(@Param("clientId") Integer clientId);

    List<RobotClientPriority> selectByParams(Map<String, Object> params);
}
