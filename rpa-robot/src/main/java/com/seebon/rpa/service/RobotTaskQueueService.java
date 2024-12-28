package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;

public interface RobotTaskQueueService {

    Integer syncTaskQueue();

    void saveTaskQueue(RobotTaskQueueVO queueVO);

    void updateTaskQueue(RobotTaskQueueVO queueVO);
}
