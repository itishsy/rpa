package com.seebon.rpa.entity.saas.vo.social;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("贫困和失业人员客户")
@Data
public class SocialCostCustVO  {

    @ApiModelProperty("客户名")
    private String custName;

    @ApiModelProperty("销售人员名字")
    private String saleName;

    private Integer id;

    @ApiModelProperty("申报状态")
    private Integer status;

}
