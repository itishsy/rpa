package com.seebon.rpa.service;

import cn.hutool.core.lang.Dict;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.*;
import com.seebon.rpa.entity.robot.vo.*;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-25 19:07:50
 */
public interface RobotClientTaskService {

    int addOrUpdate(RobotTaskDTO taskDTO);

    void callbackTaskStatus(RobotTaskVO taskVO);

    RobotTask findTask(String taskCode);

    List<RobotCommand> findCommand(String machineCode);

    void callbackCommand(RobotCommand command);

    void updateCommand(RobotCommand command);

    RobotAppDTO listTask(String machineCode);

    void callbackTask(String machineCode);

    List<RobotTaskSessionKeepVO> listTaskSessionKeep(String machineCode);

    void callbackTaskSessionKeep(String machineCode);

    List<RobotAppCaVO> listAppCa(String machineCode);

    void callbackAppCa(String machineCode);

    RobotClient getClient(String machineCode);

    void callbackClient(String machineCode);

    List<RobotConfig> listConfig(String machineCode);

    void callbackConfig(String machineCode);

    void updateTaskSessionKeep(RobotTaskSessionKeep keep);

    Dict startOrStop(StartRobotDTO dto);

    List<GetRobotStatusRespDTO> getRobotStatus(GetRobotStatusDTO dto);

    GetTaskFlowRespDTO getTaskFlow(GetTaskFlowDTO dto);

    Integer getTaskCountByCustomer(Integer clientId);
}
