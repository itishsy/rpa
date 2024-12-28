package com.seebon.rpa.entity.saas.po.system.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("短信or语音电话模板")
@Table(name = "robot_notice_template")
@Data
public class RobotNoticeTemplate extends Identity {

    @ApiModelProperty(value = "模板名称", required = true)
    @Column
    private String name;

    @ApiModelProperty(value = "模板类型，1：短信消息模板，2：语音消息模板", required = true)
    @Column
    private Integer tempType;

    @ApiModelProperty(value = "消息模板内容", required = true)
    @Column
    private String content;

    @ApiModelProperty("模板描述")
    @Column
    private String comment;

}
