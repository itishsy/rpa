package com.seebon.rpa.entity.robot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 客户机器人任务
 */
@Data
@ApiModel(value = "客户机器人任务")
@Table(name = "robot_task")
public class RobotTask implements Serializable {

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
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "执行计划")
    private String schedule;

    @Column
    @ApiModelProperty(value = "状态  0-暂停  1-启动")
    private Integer status;

    @Column
    @ApiModelProperty(value = "运行标识  0-未执行  1-执行中")
    private Integer run;

    @Column
    @ApiModelProperty(value = "同步标识  0-未同步  1-已同步")
    private Integer sync;

    @Column
    @ApiModelProperty(value = "费用机器同步标识  0-未同步  1-已同步")
    private Integer syncFee;

    @Column
    @ApiModelProperty(value = "同步机器标识码，逗号隔开")
    private String syncMachineCode;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Column
    @ApiModelProperty(value = "编辑人id")
    private String editName;

    @Column
    @ApiModelProperty(value = "执行周期")
    private String  execCycle;

    @Column
    @ApiModelProperty(value = "执行区时")
    private String  execAreaTime;

    @Column
    @ApiModelProperty(value = "执行方式：（1--连续时间区间执行；2--固定时间执行）")
    private Integer execStyle;

    @Column
    @ApiModelProperty(value = "计划类型：（1--通用计划；2--自定义计划")
    private Integer taskType;

    @Column
    private Integer execOrder;

    @Column
    @ApiModelProperty(value = "最近执行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastExecutionTime;

    @Column
    @ApiModelProperty(value = "启用时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date enableTime;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @Column
    @ApiModelProperty(value = "备注")
    private String comment;
}
