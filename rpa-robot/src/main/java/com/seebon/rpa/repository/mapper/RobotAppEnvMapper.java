package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotAppEnv;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotAppEnvMapper extends Mapper<RobotAppEnv>, MySqlMapper<RobotAppEnv> {

    void clean(@Param("appCode") String appCode);
}