package com.seebon.rpa.entity.saas.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("客户名册统计历史表")
public class CustomerInsuredRegisterExVO implements Serializable {
    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("单位社保号/公积金号")
    private String accountNumber;

    @ApiModelProperty("系统类型")
    private String tplTypeCode;

    @ApiModelProperty("名册月份")
    private Date dataMonth;

    @ApiModelProperty("名册人数")
    private Integer registerNumber;

    @ApiModelProperty("创建人id")
    private Integer createId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}
