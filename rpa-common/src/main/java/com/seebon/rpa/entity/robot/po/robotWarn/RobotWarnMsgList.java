package com.seebon.rpa.entity.robot.po.robotWarn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 14:20:48
 */
@ApiModel("rpa机器人预警消息列表")
@Data
@Table(name = "robot_warn_msg_list")
public class RobotWarnMsgList {

    @ApiModelProperty("消息id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("关联robot_warn_config_base_id 表id")
    @Column
    private Integer warnBaseId;

    @ApiModelProperty("预警类型")
    @Column
    private String warnType;

    @ApiModelProperty("预警类型名称")
    @Column
    private String warnTypeName;

    @ApiModelProperty("消息发送方式1：手机短信，2：电子邮件")
    @Column
    private Integer sendWay;

    @ApiModelProperty("消息接收人姓名")
    @Column
    private String empName;

    @ApiModelProperty("短信接收人手机号码")
    @Column
    private String phoneNumber;

    @ApiModelProperty("短信内容")
    @Column
    private String smsContent;

    @ApiModelProperty("邮件接收人电子邮箱地址")
    @Column
    private String email;

    @ApiModelProperty("邮件主题")
    @Column
    private String emailTheme;

    @ApiModelProperty("邮件内容")
    @Column
    private String emailContent;

    @ApiModelProperty("消息发送状态，0：未发送，1：发送成功，2：发送失败")
    @Column
    private Integer sendStatus;

    @ApiModelProperty("消息转发id")
    @Column
    private Integer forwardId;

    @ApiModelProperty("消息生成时间")
    @Column
    private Date createTime;

    @ApiModelProperty("消息发送时间")
    @Column
    private Date sendTime;

}
