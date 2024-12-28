package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyAccfundSuppRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-21 18:32:52
 */
@ApiModel("参保地公积金补缴规则VO")
@Data
public class PolicyAccfundSuppRuleVO extends PolicyAccfundSuppRule {

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("公积金是否允许补缴1：是，0：否")
    private Integer accfundAllowSupp;

}
