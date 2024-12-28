package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客户机器人应用执行优先级表
 */
@Data
@ApiModel(value = "客户机器人应用执行优先级表")
@Table(name = "robot_client_priority")
public class RobotClientPriority {

    @Id
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @Column
    @ApiModelProperty(value = "同步机器标识码，逗号隔开")
    private String syncMachineCode;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
