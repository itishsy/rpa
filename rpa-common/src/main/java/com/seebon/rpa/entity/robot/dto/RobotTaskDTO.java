package com.seebon.rpa.entity.robot.dto;

import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import lombok.Data;

import java.util.List;

@Data
public class RobotTaskDTO extends RobotTask {

    private List<RobotTaskArgs> argsList;

}
