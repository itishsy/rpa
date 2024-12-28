package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("机器人流程分页vo")
@Data
public class RobotAppVO1 {

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "地区")
    private String addrName;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "版本")
    private String version;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "状态（已发布，有更新未发布、草稿）")
    private int status;

    @ApiModelProperty("发布规则，主版本号，子版本号，补丁")
    private String rule;

    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("说明")
    private String comment;
}
