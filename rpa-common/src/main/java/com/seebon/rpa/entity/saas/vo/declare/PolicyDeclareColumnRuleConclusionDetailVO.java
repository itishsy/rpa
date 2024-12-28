package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnRuleConclusionDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("报盘字段规则结论动作VO")
@Data
public class PolicyDeclareColumnRuleConclusionDetailVO extends PolicyDeclareColumnRuleConclusionDetail {

    @ApiModelProperty("执行动作")
    private String typeName;

    @ApiModelProperty("动作结果解释")
    private String resultName;

}
