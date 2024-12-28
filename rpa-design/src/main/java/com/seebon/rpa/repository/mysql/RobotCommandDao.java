package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotCommand;
import com.seebon.rpa.entity.robot.vo.RobotCommandVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RobotCommandDao extends Mapper<RobotCommand> {

    List<RobotCommandVO> getRobotCommandExeList(@Param("clientId") Integer clientId);

    void updateByUuid(RobotCommand command);
}
