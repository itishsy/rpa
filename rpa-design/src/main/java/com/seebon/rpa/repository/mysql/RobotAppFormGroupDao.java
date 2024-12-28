package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotAppFormGroup;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotAppFormGroupDao extends Mapper<RobotAppFormGroup>,MySqlMapper<RobotAppFormGroup> {

    int addFormGroupWithIdentity(RobotAppFormGroup formGroup);
}
