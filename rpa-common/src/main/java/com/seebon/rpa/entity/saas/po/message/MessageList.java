package com.seebon.rpa.entity.saas.po.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("消息列表PO")
@Table(name = "message_list")
@Data
public class MessageList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("消息所属用户id")
    @Column
    private Integer userId;

    @ApiModelProperty("消息接收人姓名")
    @Column
    private String empName;

    @ApiModelProperty("所属客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("消息规则id")
    @Column
    private Integer messageRuleId;

    @ApiModelProperty("消息类型")
    @Column
    private String messageType;

    @ApiModelProperty("消息项目")
    @Column
    private String warnType;

    @ApiModelProperty("主题")
    @Column
    private String theme;

    @ApiModelProperty("邮件内容")
    @Column
    private String emailContent;

    @ApiModelProperty("收件箱")
    @Column
    private String email;

    @ApiModelProperty("邮件发送状态，1：发送成功，2：发送失败")
    @Column
    private Integer emailStatus;

    @ApiModelProperty("邮件发送失败原因")
    @Column
    private String emailFailReason;

    @ApiModelProperty("短信内容")
    @Column
    private String smsContent;

    @ApiModelProperty("接收短信手机号码")
    @Column
    private String phoneNumber;

    @ApiModelProperty("短信发送状态，1：发送成功，2：发送失败")
    @Column
    private Integer smsStatus;

    @ApiModelProperty("短信发送失败原因")
    @Column
    private String smsFailReason;

    @ApiModelProperty("公众号/企业微信发送内容")
    @Column
    private String wxContent;

    @ApiModelProperty("接收公众号/企业微信手机号码")
    @Column
    private String wxPhoneNumber;

    @ApiModelProperty("公众号/企业微信发送状态，1：发送成功，2：发送失败")
    @Column
    private Integer wxStatus;

    @ApiModelProperty("公众号/企业微信发送失败原因")
    @Column
    private String wxFailReason;

    @ApiModelProperty("消息阅读状态，0：未读，1：已读")
    @Column
    private Integer status;

    @ApiModelProperty("是否置顶，0：未读，1：已读")
    @Column
    private Integer top = 0;

    @ApiModelProperty("置顶时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date topTime;

    @ApiModelProperty("是否已删除，0：否，1：是")
    @Column
    private Integer deleted = 0;

    @ApiModelProperty("消息生成时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("修改时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
