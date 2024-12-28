package com.seebon.rpa.entity.agent.dto.declare.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("员工个人账号信息")
@Data
public class EmployeeAccountDTO implements Serializable {

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("人员个人编号信息")
    private List<EmployeeAccountDetailDTO> accountList;

}
