package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("指定任务流程信息")
@Data
public class GetTaskFlowDTO {

    @ApiModelProperty("客户Id")
    private Integer clientId;

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("业务类型 1001001：社保，1001002：公积金")
    private String businessType;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("流程标签编码")
    private String tagCode;
}
