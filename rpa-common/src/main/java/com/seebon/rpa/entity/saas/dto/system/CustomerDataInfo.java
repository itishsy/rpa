package com.seebon.rpa.entity.saas.dto.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("客户信息统计DTO")
@Data
public class CustomerDataInfo implements Serializable {

    @ApiModelProperty("可分配城市数据")
    private Integer cityTotal;

    @ApiModelProperty("已分配城市数据")
    private Integer assignedCity;

    @ApiModelProperty("可分配账户数")
    private Integer accountTotal;

    @ApiModelProperty("已分配账户数")
    private Integer assignedAccount;

    @ApiModelProperty("激活账号数")
    private Integer activeUser;

}
