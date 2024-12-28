package com.seebon.rpa.entity.saas.po.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@ApiModel("消息配置信息PO")
@Table(name = "message_rule_config")
@Data
public class MessageRuleConfig extends Identity {

    @ApiModelProperty("消息配置类型，1：运营消息，2：系统公告")
    @Column
    private Integer ruleType;

    @ApiModelProperty("消息类型，字典值 运营消息对应字典10026，系统公告对应字典10028")
    @Column
    private String messageType;

    @ApiModelProperty("消息项目，字典值 运营消息对应字典10027，系统公告对应字典10029")
    @Column
    private String warnType;

    @ApiModelProperty("响应等级，字典值 10030")
    @Column
    private String responseGrade;

    @ApiModelProperty("系统公告消息纬度城市id")
    @Column
    private Integer addrId;

    @ApiModelProperty("系统公告消息纬度城市名称")
    @Column
    private String addrName;

    @ApiModelProperty("统公告消息纬度业务类型，1：社保，2：公积金")
    @Column
    private Integer businessType;

    @ApiModelProperty("系统类型")
    @Column
    private String tplTypeCode;

    @ApiModelProperty("消息时效，1：定时消息，2：即时消息")
    @Column
    private Integer messageStrategy;

    @ApiModelProperty("发送策略，1：连续时间区间，2：固定时间点")
    @Column
    private Integer sendStrategy;

    @ApiModelProperty("发送时间")
    @Column
    private String sendTime;

    @ApiModelProperty("消息主题")
    @Column
    private String messageTopic;

    @ApiModelProperty("邮件正文")
    @Column
    private String emailContent;

    @ApiModelProperty("短信正文")
    @Column
    private String smsContent;

    @ApiModelProperty("发送邮件，1：是，0：否")
    @Column
    private Integer emailWay;

    @ApiModelProperty("发送短信，1：是，0：否")
    @Column
    private Integer smsWay;

    @ApiModelProperty("是否通知客户，1：是，0：否")
    @Column
    private Integer sendCust;

    @ApiModelProperty("是否通知客户备注信息")
    @Column
    private String custComment;

    @ApiModelProperty("是否通知运营人员，1：是，0：否")
    @Column
    private Integer sendOperator;

    @ApiModelProperty("是否通知运营备注信息")
    @Column
    private String operatorComment;

    @ApiModelProperty("是否通知指定人员，1：是，0：否")
    @Column
    private Integer sendPersonnel;

    @ApiModelProperty("是否通知指定人员备注信息")
    @Column
    private String personnelComment;

    @ApiModelProperty("状态，0：草稿，1：生效中或已发布，2：已失效")
    @Column
    private Integer status;

    @ApiModelProperty("系统公告是否已消费，1：是，0：否")
    @Column
    private Integer consumed;

    @ApiModelProperty("概述")
    @Column
    private String comment;

    @ApiModelProperty("发布人id")
    @Column
    private Integer releaseId;

    @ApiModelProperty("发布时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

}
