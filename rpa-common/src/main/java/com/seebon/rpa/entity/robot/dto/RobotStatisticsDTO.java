package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("运营后台首页盒子-申报账户统计信息DTO")
@Data
public class RobotStatisticsDTO implements Serializable {

    @ApiModelProperty("总盒子数")
    private Integer machineTotal = 0;

    @ApiModelProperty("执行中的盒子数")
    private Integer execMachines = 0;

    @ApiModelProperty("设备异常数")
    private Integer abnormalMachines = 0;

    @ApiModelProperty("申报账户数")
    private Integer accountTotal = 0;

    @ApiModelProperty("执行中账户数")
    private Integer runningAccounts = 0;

    @ApiModelProperty("已停用账户数")
    private Integer stopAccounts = 0;

}
