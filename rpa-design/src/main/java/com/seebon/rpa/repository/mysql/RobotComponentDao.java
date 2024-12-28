package com.seebon.rpa.repository.mysql;


import com.seebon.rpa.entity.robot.RobotComponent;
import com.seebon.rpa.entity.robot.dto.design.RobotComponentVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface RobotComponentDao extends Mapper<RobotComponent> {

    int addComponent(RobotComponentVO robotComponentVO);

    List<RobotComponent> selectByAll();

    List<RobotComponent> selectByHistory(@Param("component") String component);

    int updateStatus(@Param("component") String component);

    int updateStatusIsStop(@Param("component") String component);

    int enableStatus(@Param("id") Integer id);

    int stopStatus(@Param("id") Integer id);

    RobotComponentVO selectVersion(@Param("component") String component);

    RobotComponent selectComponent(@Param("component") String component);

    List<RobotComponent> selectByComment(String comment);

    List<RobotComponentVO> selectNewVersion(@Param("component") String component);
}
