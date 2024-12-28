package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotTaskNotice;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface RobotTaskNoticeDao extends Mapper<RobotTaskNotice>, MySqlMapper<RobotTaskNotice> {

    Integer updateRegainStatus(@Param("regainStatus") Integer regainStatus, @Param("taskCode") String taskCode);

    RobotTaskNotice selectByTaskCode(@Param("taskCode") String taskCode);
}