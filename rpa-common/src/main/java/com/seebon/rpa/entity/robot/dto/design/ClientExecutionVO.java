package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("机器人客户端监控执行任务vo")
@Data
public class ClientExecutionVO {

    @ApiModelProperty(value = "地区")
    private String address;

    @ApiModelProperty(value = "机器人地区")
    private String appName;

    @ApiModelProperty(value = "业务")
    private String businessType;

    @ApiModelProperty(value = "RPA流程名称")
    private String flowName;

    @ApiModelProperty(value = "帐号主体名称")
    private String customerName;

    @ApiModelProperty(value = "执行时间")
    private Date startTime;

    @ApiModelProperty(value = "状态")
    private String error;

    @ApiModelProperty(value = "执行编码")
    private String executionCode;

}
