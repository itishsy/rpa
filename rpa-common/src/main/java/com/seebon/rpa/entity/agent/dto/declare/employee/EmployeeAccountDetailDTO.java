package com.seebon.rpa.entity.agent.dto.declare.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("员工个人账号明细")
@Data
public class EmployeeAccountDetailDTO implements Serializable {

    @ApiModelProperty("人员证件号码")
    private String idCard;

    @ApiModelProperty("个人编号（公积金号或社保号）")
    private String empAccount;

}
