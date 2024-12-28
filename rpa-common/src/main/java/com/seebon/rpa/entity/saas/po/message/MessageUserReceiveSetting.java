package com.seebon.rpa.entity.saas.po.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("客户用户消息接收配置信息PO")
@Table(name = "message_user_receive_setting")
@Data
public class MessageUserReceiveSetting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("消息类型")
    @Column
    private String messageType;

    @ApiModelProperty("消息项目")
    @Column
    private String warnType;

    @ApiModelProperty("消息所属用户id")
    @Column
    private Integer userId;

    @ApiModelProperty("所属客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("邮件通知，1：开启，0：关闭")
    @Column
    private Integer emailWay;

    @ApiModelProperty("短信通知，1：开启，0：关闭")
    @Column
    private Integer smsWay;

}
