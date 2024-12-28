package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("机器人客户端监控appVO")
@Data
public class RobotClientFlowVO {

    @ApiModelProperty(value = "帐号主体名称")
    private String customerName;

    @ApiModelProperty(value = "RPA流程名称")
    private String flowName;

    @ApiModelProperty(value = "客户端版本")
    private String version;

    @ApiModelProperty(value = "最新版本")
    private String newVersion;

    @ApiModelProperty(value = "操作")
    private String operation;

}
