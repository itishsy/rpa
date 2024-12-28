package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RobotFlowDesignExVO {
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "流程code")
    private String flowCode;

    @ApiModelProperty(value = "步骤code")
    private String stepCode;

    @ApiModelProperty(value = "模板流程code")
    private String templateFlowCode;
}
