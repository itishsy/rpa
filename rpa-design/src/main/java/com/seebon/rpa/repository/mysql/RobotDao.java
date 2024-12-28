package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.Robot;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/8/25 16:07
 * @Version 1.0
 **/
public interface RobotDao extends Mapper<Robot> {

    String selectRobotCodeByComment(String comment);
}
