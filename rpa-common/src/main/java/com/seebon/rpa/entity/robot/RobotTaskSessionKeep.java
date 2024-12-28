package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "客户机器人任务长seeion维持")
@Table(name = "robot_task_session_keep")
public class RobotTaskSessionKeep implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "申报系统 字典 10007,10008")
    private String declareSystem;

    @Column
    @ApiModelProperty(value = "备注")
    private String comment;

    @Column
    @ApiModelProperty(value = "远程端口号")
    private Integer port;

    @Column
    @ApiModelProperty(value = "远程端口号")
    private Integer sharePort;

    @Column
    @ApiModelProperty(value = "维持状态：1-浏览器关闭 2-会话丢失 3-会话维持")
    private Integer status;

    @Column
    @ApiModelProperty(value = "维持访问链接")
    private String url;

    @Column
    @ApiModelProperty(value = "校验是否当前单位会话维持")
    private String xpath;

    @Column
    @ApiModelProperty(value = "会话丢失xpath")
    private String loseXpath;

    @Column
    @ApiModelProperty(value = "匹配表达式")
    private String matchExpress;

    @Column
    @ApiModelProperty(value = "刷新重试次数")
    private Integer retryNum;

    @Column
    @ApiModelProperty(value = "会话失效截图文件Id")
    private Integer fileId;

    @Column
    @ApiModelProperty(value = "同步标识  0-未同步  1-已同步")
    private Integer sync;

    @Column
    @ApiModelProperty(value = "登录状态  0-未登录  1-已登录")
    private Integer loginStatus;

    @Column
    @ApiModelProperty(value = "同步机器标识码，逗号隔开")
    private String syncMachineCode;

    @Column
    @ApiModelProperty(value = "最长维持时间(单位小时)")
    private Integer maxKeepTime;

    @Column
    @ApiModelProperty(value = "启用状态：0-禁用，1-启用")
    private Integer disabled;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "会话维持开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startKeepTime;

    @Column
    @ApiModelProperty(value = "会话失效时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endKeepTime;

    @Column
    @ApiModelProperty(value = "创建者Id")
    private Integer createId;

    @Column
    @ApiModelProperty(value = "创建者名称")
    private String createName;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
