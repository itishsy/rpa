package com.seebon.rpa.entity.robot.po.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * TODO
 *
 * @author zjf
 * @describe 消息配置表
 * @date 2023/4/21 9:48
 */
@Data
@ApiModel("消息配置表")
@Table(name = "robot_operation_message_config")
public class RobotOperationMessageConfig{
    @Column
    @ApiModelProperty("id")
    private Integer id;

    @Column
    @ApiModelProperty("消息类型")
    private String messageType;

    @Column
    @ApiModelProperty("消息类型")
    private String warnType;

    @Column
    @ApiModelProperty("消息名称")
    private String warnName;

    @Column
    @ApiModelProperty("响应等级")
    private Integer responseGrade;

    @Column
    @ApiModelProperty("消息主题")
    private String messageTopic;

    @Column
    @ApiModelProperty("消息时效")
    private String messageStrategy;

    @Column
    @ApiModelProperty("通知对象id")
    private Integer personTypeId;

    @Column
    @ApiModelProperty("通知对象")
    private String personGroup;

    @Column
    @ApiModelProperty("通知对象方式")
    private String sendWay;

    @Column
    @ApiModelProperty("短信内容")
    private String smsContent;

    @Column
    @ApiModelProperty("弹框内容")
    private String boxContent;

    @Column
    @ApiModelProperty("邮件内容")
    private String emailContent;

    @Column
    @ApiModelProperty("短信发送状态")
    private int smsSendStatus;

    @Column
    @ApiModelProperty("邮件发送状态")
    private int emailSendStatus;

    @Column
    @ApiModelProperty("消息状态")
    private int messageStatus;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
