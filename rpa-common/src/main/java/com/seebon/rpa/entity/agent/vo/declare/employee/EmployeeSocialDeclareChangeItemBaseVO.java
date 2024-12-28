package com.seebon.rpa.entity.agent.vo.declare.employee;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeItemBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author hao
 * @Date 2022/4/24 16:09
 * @Version 1.0
 **/

@Data
@ApiModel("")
public class EmployeeSocialDeclareChangeItemBaseVO extends EmployeeSocialDeclareChangeItemBase {
    @ApiModelProperty("相同申报状态的参保险种")
    private String sameStatusName;

    @ApiModelProperty("社保号")
    private String socialnum;

    @ApiModelProperty("item_base表id")
    private String itemId;

    private String idCard;

}
