package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAppEnv;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface RobotAppEnvDao extends Mapper<RobotAppEnv> {

    List<RobotAppEnv> selectByParams(Map<String, Object> params);

}