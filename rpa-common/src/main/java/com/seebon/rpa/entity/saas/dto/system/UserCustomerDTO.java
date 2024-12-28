package com.seebon.rpa.entity.saas.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("运营用户客户权限信息")
@Data
public class UserCustomerDTO implements Serializable {

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("客户名称")
    private String custName;

    @ApiModelProperty("客户参保城市数")
    private Integer cityNumber;

    @ApiModelProperty("客户社保申报账户数")
    private Integer socialAccountNumber;

    @ApiModelProperty("客户公积金申报账户数")
    private Integer accfundAccountNumber;

    @ApiModelProperty("是否已分配")
    private Boolean checked;

}
