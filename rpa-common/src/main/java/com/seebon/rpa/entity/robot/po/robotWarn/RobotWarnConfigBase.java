package com.seebon.rpa.entity.robot.po.robotWarn;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 14:09:31
 */
@ApiModel("rpa机器人预警基础配置信息")
@Table(name = "robot_warn_config_base")
@Data
public class RobotWarnConfigBase extends Identity {

    @ApiModelProperty("预警类型")
    @Column
    private String type;

    @ApiModelProperty("预警类型名称")
    @Column
    private String typeName;

    @ApiModelProperty("开启短信提醒，1：开启，0：关闭")
    @Column
    private Integer sendSms;

    @ApiModelProperty("短信消息内容")
    @Column
    private String smsContent;

    @ApiModelProperty("开启邮箱提醒，1：开启，0：关闭")
    @Column
    private Integer sendEmail;

    @ApiModelProperty("邮件主题")
    @Column
    private String emailTheme;

    @ApiModelProperty("邮件内容")
    @Column
    private String emailContent;

    @ApiModelProperty("预警频率")
    @Column
    private String cron;

    @ApiModelProperty("预警频率描述")
    @Column
    private String cronComment;

    @ApiModelProperty("预警状态，1：开启，0：关闭")
    @Column
    private Integer status;

    @ApiModelProperty("检索异常数据时间范围，单位分钟")
    @Column
    private Integer rangeTime;

}
