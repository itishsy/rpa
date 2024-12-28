package com.seebon.rpa.entity.agent.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("客户申报单位业务类型信息")
@Data
public class CustomerOrgBusinessDTO implements Serializable {

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("是否有H5登录，1：是，0：否")
    private Integer isH5Login;

    // 申报账户状态  0--暂停  1--启动
    @ApiModelProperty("是否有H5登录，0--暂停  1--启动")
    private Integer status;

    // 申报账户状态  0--暂停  1--启动
    @ApiModelProperty("原因备注")
    private String comment;

}
