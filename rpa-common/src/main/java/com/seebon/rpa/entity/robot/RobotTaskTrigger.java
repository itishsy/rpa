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
@ApiModel(value = "客户机器人任务生成计划")
@Table(name = "robot_task_trigger")
public class RobotTaskTrigger implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @Column
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @Column
    @ApiModelProperty(value = "服务项目名称")
    private String serviceItemName;

    @Column
    @ApiModelProperty(value = "任务生成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date generatePlan;

    @Column
    @ApiModelProperty(value = "下次执行时间")
    private Integer nextHour;

    @Column
    @ApiModelProperty(value = "状态：生成任务标识  0-未生成  1-已生成  2-完成")
    private Integer status;

    @Column
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

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
