package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotAppCa;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotAppCaMapper extends Mapper<RobotAppCa>, MySqlMapper<RobotAppCa> {

    void cleanByAppCode(@Param("appCode") String appCode, @Param("declareSystem") String declareSystem);
}
