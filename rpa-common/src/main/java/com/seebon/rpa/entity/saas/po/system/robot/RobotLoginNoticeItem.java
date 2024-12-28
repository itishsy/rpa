package com.seebon.rpa.entity.saas.po.system.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("机器人登录推送消息明细表")
@Table(name = "robot_login_notice_item")
@Data
public class RobotLoginNoticeItem implements Serializable {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("机器人执行批次号")
    @Column
    private String execBatchCode;

    @ApiModelProperty("二维码图片文件id 对应表 sys_filestore id")
    @Column
    private Integer imgFileId;

    @ApiModelProperty("短信验证码")
    @Column
    private String smsCode;

    @ApiModelProperty("本次有效期时长，单位秒")
    @Column
    private Integer singleAgeing;

    @ApiModelProperty("状态 0：待验证，1：已验证，2：已过期")
    @Column
    private Integer status;

    @ApiModelProperty("发送时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("修改时间，用户回写短信验证码的时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
