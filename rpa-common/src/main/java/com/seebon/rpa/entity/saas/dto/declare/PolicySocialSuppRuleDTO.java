package com.seebon.rpa.entity.saas.dto.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicySocialSuppRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-21 18:34:35
 */
@ApiModel("参保地社保补缴规则DTO")
@Data
public class PolicySocialSuppRuleDTO {

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("社保是否允许补缴1：是，0：否")
    private Integer socialAllowSupp;

    @ApiModelProperty("社保补缴规则")
    private List<PolicySocialSuppRule> rules;

}
