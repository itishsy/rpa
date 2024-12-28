package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.dto.ChangeLoginStatusDTO;
import com.seebon.rpa.entity.robot.dto.EmployeeChangeTrackDTO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.entity.robot.vo.RobotTrackVO;

import java.util.List;
import java.util.Map;

public interface RobotTaskQueueService {
    IgGridDefaultPage<RobotTaskQueueVO> page(Map<String, Object> map);

    Integer toTop(Integer id);

    Integer revoke(String ids);

    void stopTask(String taskCode, String comment);

    Map<String, Object> addTaskQueue(RobotTaskQueueVO queueVO);

    RobotTaskQueueVO findTaskQueue(String machineCode);

    int callbackTaskQueue(List<RobotTaskQueue> list);

    RobotTrackVO getTrackStatus(EmployeeChangeTrackDTO trackDTO);

    RobotTrackVO getTrackList(EmployeeChangeTrackDTO trackDTO);

    void updatePreTime(Integer clientId, Integer sort);

    String getMachineCode(RobotTaskQueue taskQueue);

    void updateLoginStatus(ChangeLoginStatusDTO statusDTO);

    void cleanTaskQueue();
}
