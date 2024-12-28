package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotConfig;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotConfigMapper extends Mapper<RobotConfig>, MySqlMapper<RobotConfig> {

    void cleanByAppCode(@Param("appCode") String appCode, @Param("declareSystem") String declareSystem);
}
