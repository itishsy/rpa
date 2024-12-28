package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("历史版本vo")
@Data
public class RobotAppHistoryVO {

    @ApiModelProperty(value="应用编码")
    private String appCode;

    @ApiModelProperty(value="版本")
    private String version;

    @ApiModelProperty(value="版本说明")
    private String comment;

    @ApiModelProperty(value = "变更原因")
    private String changeReason;

    @ApiModelProperty(value="发布时间")
    private Date releaseTime;

    @ApiModelProperty(value="状态")
    private int status;

    @ApiModelProperty(value="发布状态: 1、已发布 0、未发布 ")
    private int runStatus;

    @ApiModelProperty(value = "发布规则")
    private String rule;

    @ApiModelProperty(value = "发布人")
    private String releaseName;
}
