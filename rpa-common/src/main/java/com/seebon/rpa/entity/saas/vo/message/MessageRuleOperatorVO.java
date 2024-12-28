package com.seebon.rpa.entity.saas.vo.message;

import com.seebon.rpa.entity.saas.po.message.MessageRuleOperator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("消息通知运营对象VO")
@Data
public class MessageRuleOperatorVO extends MessageRuleOperator {

    @ApiModelProperty("运营角色名称")
    private String roleName;

}
