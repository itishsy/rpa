package com.seebon.rpa.repository.mysql;


import com.seebon.rpa.entity.robot.RobotClientTrack;
import tk.mybatis.mapper.common.Mapper;


public interface RobotClientTrackDao extends Mapper<RobotClientTrack> {

    RobotClientTrack selectLatest(String machineCode);
}
