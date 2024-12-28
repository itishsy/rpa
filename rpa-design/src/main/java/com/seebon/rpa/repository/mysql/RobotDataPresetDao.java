package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotDataPreset;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RobotDataPresetDao extends Mapper<RobotDataPreset> {

    @Select("select * from robot_data_preset where type = #{type}")
    List<RobotDataPreset> findDataPreset(Integer type);

    @Select("select * from robot_data_preset where type = #{type} limit 1")
    RobotDataPreset getByType(Integer type);
}
