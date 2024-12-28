package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotApp;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface RobotAppMapper extends Mapper<RobotApp> {

    void clean(@Param("appCode") String appCode);

    void cleanFlowTemplate(@Param("flowCode") String flowCode);

    RobotApp findApp(@Param("taskCode") String taskCode);
}
