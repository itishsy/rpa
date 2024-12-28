package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RobotFlowDesignCopyVO {
    @ApiModelProperty(value = "源流程code")
    private String sourceFlowCode;

    @ApiModelProperty(value = "目标流程code")
    private String targetFlowCode;

    @ApiModelProperty(value = "步骤code")
    private String stepCode;

    @ApiModelProperty(value = "上一流程code")
    private String parentFlowCode;
}
