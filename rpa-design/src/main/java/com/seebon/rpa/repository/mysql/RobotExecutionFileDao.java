package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotExecutionFile;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface RobotExecutionFileDao extends Mapper<RobotExecutionFile> {

    List<RobotExecutionFile> selectList(Map<String,Object> params);

}