package com.seebon.rpa.entity.saas.vo.declare.account;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountVO {

    @ApiModelProperty("单位社保号或公积金号")
    private String accountNumber;

    @ApiModelProperty("账号记录id")
    private Integer accountId;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("数据id")
    private String id;
}
