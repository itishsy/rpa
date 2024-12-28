package com.seebon.rpa.entity.saas.po.bill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-04 18:15
 */
@ApiModel(value ="收款单项目明细")
@Data
public class ReceiptProjectDetail{

    @ApiModelProperty("申报单位")
    private String declareUnit;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("在保人数")
    private Integer registerNumber;
}
