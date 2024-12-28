package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotInputType;
import tk.mybatis.mapper.common.Mapper;

public interface RobotInputTypeDao extends Mapper<RobotInputType> {
    void insertInfo(RobotInputType robotInputType);
}
