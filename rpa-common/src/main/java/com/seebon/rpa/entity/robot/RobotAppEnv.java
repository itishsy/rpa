package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Table(name = "robot_app_env")
public class RobotAppEnv implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @Column
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty(value = "证书安装路径")
    private String path;

    @Column
    @ApiModelProperty(value = "下载链接")
    private String downloadUrl;

    @Column
    @ApiModelProperty(value = "类型（0-需启动(证书)，1-无需启动（浏览器插件））")
    private Integer type;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    //==============拓展====================//
    @Transient
    @ApiModelProperty(value = "流程名称")
    private String flowName;
}