package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnRule;
import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnRuleConditionDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("申报字段规则VO")
@Data
public class PolicyDeclareColumnRuleVO extends PolicyDeclareColumnRule {

    @ApiModelProperty("结论")
    private List<PolicyDeclareColumnRuleConclusionVO> ruleConclusions;

    @ApiModelProperty("规则条件信息")
    private List<PolicyDeclareColumnRuleConditionDetail> ruleConditionDetails;

}
