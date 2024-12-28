package com.seebon.rpa.entity.saas.po.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("消息通知指定人员信息PO")
@Table(name = "message_rule_personnel")
@Data
public class MessageRulePersonnel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("对应表message_rule_config id")
    @Column
    private Integer messageRuleId;

    @ApiModelProperty("姓名")
    @Column
    private String name;

    @ApiModelProperty("手机号码")
    @Column
    private String phoneNumber;

    @ApiModelProperty("电子邮箱")
    @Column
    private String email;
}
