package com.seebon.rpa.entity.saas.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("运营用户参保城市权限信息")
@Data
public class UserAddrDTO implements Serializable {

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("参保城市所在省")
    private String provinceName;

    @ApiModelProperty("客户社保申报账户数")
    private Integer custNumber;

    @ApiModelProperty("客户社保申报账户数")
    private Integer socialAccountNumber;

    @ApiModelProperty("客户公积金申报账户数")
    private Integer accfundAccountNumber;

    @ApiModelProperty("是否已分配")
    private Boolean checked;

}
