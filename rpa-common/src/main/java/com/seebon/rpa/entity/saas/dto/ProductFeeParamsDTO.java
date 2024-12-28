package com.seebon.rpa.entity.saas.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductFeeParamsDTO implements Serializable {

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("参保基数")
    private String baseAmount;

    @ApiModelProperty("个人公积金比例")
    private String empRatio;

    @ApiModelProperty("单位公积金比例")
    private String compRatio;

    @ApiModelProperty("个人补充公积金比例")
    private String suppEmpRatio;

    @ApiModelProperty("单位补充公积金比例")
    private String suppCompRatio;

    @ApiModelProperty("业务类型：1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("开始年月")
    private String startMonth;

    @ApiModelProperty("结束年月")
    private String endMonth;

}
