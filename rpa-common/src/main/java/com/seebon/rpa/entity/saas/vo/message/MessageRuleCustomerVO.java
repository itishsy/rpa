package com.seebon.rpa.entity.saas.vo.message;

import com.seebon.rpa.entity.saas.po.message.MessageRuleCustomer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("消息通知客户对象VO")
@Data
public class MessageRuleCustomerVO extends MessageRuleCustomer {

    @ApiModelProperty("客户名称")
    private String custName;

}
