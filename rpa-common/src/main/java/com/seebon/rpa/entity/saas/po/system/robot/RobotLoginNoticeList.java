package com.seebon.rpa.entity.saas.po.system.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("机器人登录推送消息列表")
@Table(name = "robot_login_notice_list")
@Data
public class RobotLoginNoticeList implements Serializable {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("机器人执行批次号")
    @Column
    private String execBatchCode;

    @ApiModelProperty("通知模板id，对应robot_login_notice_template id")
    @Column
    private Integer tempId;

    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("公司名称")
    @Column
    private String companyName;

    @ApiModelProperty("单位社保号/公积金号")
    @Column
    private String accountNumber;

    @ApiModelProperty("发送手机号码")
    @Column
    private String recipientPhoneNumber;

    @ApiModelProperty("发送状态 0：待发送，1：已发送，2：发送失败")
    @Column
    private Integer sendStatus;

    @ApiModelProperty("推送公众号消息状态，0：待推送，1：推送成功，2：推送失败")
    @Column
    private Integer wxSendStatus;

    @ApiModelProperty("推送公众号消息失败原因")
    @Column
    private String wxSendFailReason;

    @ApiModelProperty("状态 0：待验证，1：已验证，2：已过期")
    @Column
    private Integer status;

    @ApiModelProperty("应用code")
    @Column
    private String appCode;

    @ApiModelProperty("应用名称")
    @Column
    private String appName;

    @ApiModelProperty("链接有效期，单位秒")
    @Column
    private Integer linkAgeing;

    @ApiModelProperty("网址地址")
    @Column
    private String netAddress;

    @ApiModelProperty("验证方式 对应字典值10021")
    @Column
    private String validateType;

    @ApiModelProperty("是否短信提醒，1：是，0：否")
    @Column
    private Integer smsReminder;

    @ApiModelProperty("短信内容")
    @Column
    private String smsContent;

    @ApiModelProperty("是否语音提醒，1：是，0：否")
    @Column
    private Integer voiceReminder;

    @ApiModelProperty("语音内容")
    @Column
    private String voiceContent;

    @ApiModelProperty("提示信息")
    @Column
    private String tips;

    @ApiModelProperty("是否重新获取验证码 1：是，0：否")
    @Column
    private Integer retrieve;

    @ApiModelProperty("发送时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
