package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotAppConfigGroup;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotAppGroupMapper extends Mapper<RobotAppConfigGroup>, MySqlMapper<RobotAppConfigGroup> {
    List<RobotAppConfigGroup> selectGroupByIds(List<Integer> ids);
}
