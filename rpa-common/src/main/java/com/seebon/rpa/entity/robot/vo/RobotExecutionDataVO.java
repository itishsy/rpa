package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotExecutionData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "机器人执行记录")
public class RobotExecutionDataVO extends RobotExecutionData {

    @ApiModelProperty(value = "id")
    private Integer outId;
}
