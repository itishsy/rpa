package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RobotFlowOpenTypeVO {
    @ApiModelProperty(value = "打开方式：0 流程步骤 1 流程图")
    private Integer openType;

    @ApiModelProperty(value = "流程code")
    private String flowCode;

    @ApiModelProperty(value = "模板流程code")
    private String templateFlowCode;
}
