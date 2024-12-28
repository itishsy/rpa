package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 客户机器人配置表
 */
@Data
@ApiModel(value = "客户机器人配置表")
@Table(name = "robot_config")
public class RobotConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "配置项")
    private String key;

    @Column
    @ApiModelProperty(value = "配置内容")
    private String context;

    @Column
    @ApiModelProperty(value = "备注")
    private String remark;

    @Column
    @ApiModelProperty(value = "启用状态：0-禁用，1-启用")
    private Integer disabled;

    @Column
    @ApiModelProperty(value = "同步机器标识码，逗号隔开")
    private String syncMachineCode;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
