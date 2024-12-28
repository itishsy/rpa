package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeAccfundDeclareChangeItemBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("")
public class EmployeeAccfundDeclareChangeItemBaseVO extends EmployeeAccfundDeclareChangeItemBase {
    @ApiModelProperty("相同申报状态的参保险种")
    private String sameStatusName;

    private String socialnum;

}
