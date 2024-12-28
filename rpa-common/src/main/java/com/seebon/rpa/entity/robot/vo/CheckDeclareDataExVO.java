package com.seebon.rpa.entity.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CheckDeclareDataExVO {

    @ApiModelProperty(value = "客户ID")
    private Integer custId;

    @ApiModelProperty(value = "申报账户")
    private String accountNumber;

    @ApiModelProperty(value = "地址")
    private String addrName;

    @ApiModelProperty(value = "增员数")
    private Integer addCount;

    @ApiModelProperty(value = "减员数")
    private Integer stopCount;

    @ApiModelProperty(value = "调基数")
    private Integer adjustCount;

    @ApiModelProperty(value = "补缴数")
    private Integer suppCount;
}
