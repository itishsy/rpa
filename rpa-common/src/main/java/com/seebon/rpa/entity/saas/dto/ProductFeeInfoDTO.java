package com.seebon.rpa.entity.saas.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@ApiModel("方案费用计算信息")
@Data
public class ProductFeeInfoDTO implements Serializable {

    @ApiModelProperty("单位缴纳金额")
    private String compAmount;

    @ApiModelProperty("个人缴纳金额")
    private String empAmount;

    @ApiModelProperty("月缴纳金额")
    private String monthAmount;

    @ApiModelProperty("月缴纳金额小计")
    private String totalAmount;

}
