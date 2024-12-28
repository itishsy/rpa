package com.seebon.rpa.entity.saas.po.system.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("机器人登录提醒模板配置信息")
@Table(name = "robot_login_notice_template")
@Data
public class RobotLoginNoticeTemplate extends Identity {

    @ApiModelProperty(value = "应用编码", required = true)
    @Column
    private String appCode;

    @ApiModelProperty(value = "应用名称", required = true)
    @Column
    private String appName;

    @ApiModelProperty(value = "系统类型 对应字典值10007、10008", required = true)
    @Column
    private String tplTypeCode;

    @ApiModelProperty(value = "网址地址", required = true)
    @Column
    private String netAddress;

    @ApiModelProperty(name = "推送方式 对应字典值10021", required = true)
    @Column
    private String validateType;

    @ApiModelProperty(value = "是否语音提醒，1：是，0：否", required = true)
    @Column
    private Integer voiceReminder;

    @ApiModelProperty(value = "语音模板id，需要语音提醒时必填")
    @Column
    private Integer voiceTempId;

    @ApiModelProperty(value = "是否短信提醒，1：是，0：否", required = true)
    @Column
    private Integer smsReminder;

    @ApiModelProperty(value = "短信模板id, 需要短信提醒是必填")
    @Column
    private Integer smsTempId;

    @ApiModelProperty(value = "等待时长，单位秒", required = true)
    @Column
    private Integer singleAgeing;

    @ApiModelProperty(value = "链接有效期，单位秒", required = true)
    @Column
    private Integer linkAgeing;

}
