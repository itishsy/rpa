package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.dto.RobotExecutionDTO;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RobotExecutionMapper extends Mapper<RobotExecution>, IdsMapper<RobotExecution> {

    List<RobotExecutionDTO> selectUnSyncExecution();

    void updateSyncStatus(Integer id);

    List<Integer> selectSyncExecution(String syncTime);
}
