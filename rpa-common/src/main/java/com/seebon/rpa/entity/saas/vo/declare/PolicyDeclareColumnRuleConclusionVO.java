package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnRuleConclusion;
import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnRuleConclusionDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("报盘字段规则结论信息VO")
@Data
public class PolicyDeclareColumnRuleConclusionVO extends PolicyDeclareColumnRuleConclusion {

    @ApiModelProperty("校验板块名称")
    private String moduleName;

    @ApiModelProperty("校验作用对象名称")
    private String targetAudienceName;

    @ApiModelProperty("结论动作")
    private List<PolicyDeclareColumnRuleConclusionDetailVO> ruleConclusionDetails;

}
