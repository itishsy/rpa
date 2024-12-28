package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("机器人执行状态查询DTO")
@Data
public class GetRobotStatusDTO {

    @ApiModelProperty("平台ID")
    private String platform;

    @ApiModelProperty("客户ID")
    private Integer customerId;

    //================无须传递====================//
    @ApiModelProperty("客户IDs")
    private List<Integer> customerIds;

    @ApiModelProperty("taskCodes")
    private List<String> taskCodes;
}
