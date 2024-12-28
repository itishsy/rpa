package com.seebon.rpa.entity.saas.po.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("消息通知运营对象PO")
@Table(name = "message_rule_operator")
@Data
public class MessageRuleOperator implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("对应表message_rule_config id")
    @Column
    private Integer messageRuleId;

    @ApiModelProperty("运营角色id")
    @Column
    private Integer roleId;

}
