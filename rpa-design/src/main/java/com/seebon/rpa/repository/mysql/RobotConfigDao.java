package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotConfig;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RobotConfigDao extends Mapper<RobotConfig>, MySqlMapper<RobotConfig> {

    List<RobotConfig> selectByMachineCode(@Param("machineCode") String machineCode);

    void againSync(@Param("id") Integer id);
}