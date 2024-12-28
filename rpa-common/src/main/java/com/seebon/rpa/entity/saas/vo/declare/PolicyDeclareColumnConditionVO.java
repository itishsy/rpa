package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnCondition;
import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnConditionItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-13 17:45:19
 */
@ApiModel("报盘字段条件VO")
@Data
public class PolicyDeclareColumnConditionVO extends PolicyDeclareColumnCondition {

    @ApiModelProperty("报盘字段条件明细信息")
    private List<PolicyDeclareColumnConditionItem> conditionItemList;

}
