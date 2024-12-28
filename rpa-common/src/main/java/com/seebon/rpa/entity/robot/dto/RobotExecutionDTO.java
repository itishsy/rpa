package com.seebon.rpa.entity.robot.dto;

import com.seebon.rpa.entity.robot.*;
import lombok.Data;

import java.util.List;

@Data
public class RobotExecutionDTO extends RobotExecution {

    private List<RobotExecutionDetail> details;

}
