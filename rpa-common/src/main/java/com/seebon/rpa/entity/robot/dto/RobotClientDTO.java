package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("机器人设备信息DTO")
@Data
public class RobotClientDTO implements Serializable {

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "设备编号")
    private String machineCode;

    @ApiModelProperty(value = "设备厂商")
    private String machineFactory;

    @ApiModelProperty(value = "设备状态")
    private String status;

    @ApiModelProperty(value = "申报账户数")
    private Integer accountNumber;

    @ApiModelProperty(value = "应用下的申报账户数")
    private Integer appAccountNumber;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "正在执行数")
    private Integer runningCount;

    @ApiModelProperty(value = "已停用数")
    private Integer stopCount;

    @ApiModelProperty(value = "启用数")
    private Integer startCount;

    @ApiModelProperty(value = "应用主流程数")
    private Integer flowCount;

}
