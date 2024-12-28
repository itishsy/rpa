package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("机器人应用信息DTO")
@Data
public class RobotAppInfoDTO implements Serializable {

    @ApiModelProperty("应用编号")
    private String appCode;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("应用上线阶段编码")
    private Integer onlineStatus;

    @ApiModelProperty("应用上线阶段名称")
    private String onlineStatusName;

}
