package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("机器人执行状态查询DTO")
@Data
public class GetRobotStatusRespDTO {

    @ApiModelProperty("平台ID")
    private String platform;

    @ApiModelProperty("客户ID")
    private Integer customerId;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型：1001001-社保、1001002-公积金")
    private String businessType;

    @ApiModelProperty("单位名称")
    private String companyName;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("执行状态：0-未执行 1-执行中")
    private Integer runStatus;
}
