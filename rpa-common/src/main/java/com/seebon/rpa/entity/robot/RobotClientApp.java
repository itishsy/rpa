package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客户机器人应用
 */
@Data
@ApiModel(value = "客户机器人应用")
@Table(name = "robot_client_app")
public class RobotClientApp {

    @Id
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
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @Column
    @ApiModelProperty(value = "版本号")
    private String version;

    @Column
    @ApiModelProperty(value = "状态（0-未登录；1-启用；2-停用）")
    private Integer status;

    @Column
    @ApiModelProperty(value = "指定此设备执行 0-否 1-是")
    private Integer fixMachine;

    @Column
    @ApiModelProperty(value = "指定执行原因")
    private String fixRemark;

    @Column
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
