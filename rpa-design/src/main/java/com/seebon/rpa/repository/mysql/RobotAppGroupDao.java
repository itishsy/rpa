package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAppConfigGroup;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RobotAppGroupDao extends Mapper<RobotAppConfigGroup> {
    List<RobotAppConfigGroup> selectGroupByIds(List<Integer> ids);
}
