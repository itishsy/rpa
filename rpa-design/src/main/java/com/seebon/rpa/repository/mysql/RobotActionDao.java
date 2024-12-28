package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAction;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RobotActionDao extends Mapper<RobotAction> {

    List<RobotAction> findAll();

}
