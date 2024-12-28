package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("启停机器人DTO")
@Data
public class StartRobotDTO {

    @ApiModelProperty("启停类型：0-暂停 1-启动")
    private Integer status;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型：1001001-社保、1001002-公积金")
    private String businessType;

    @ApiModelProperty("申报账号")
    private String accountNumber;

    //================无须传递====================//
    @ApiModelProperty("参保城市Id")
    private Integer addrId;
}
