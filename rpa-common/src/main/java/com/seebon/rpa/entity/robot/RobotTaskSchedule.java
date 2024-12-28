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
 * @Author hao
 * @Date 2022/9/27 13:49
 * @Version 1.0
 **/
@Data
@Table(name = "robot_task_schedule")
@ApiModel("流程执行计划")
public class RobotTaskSchedule implements Serializable {

    @ApiModelProperty("主键Id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("关联robot_general_plan表id")
    @Column
    private Integer generalPlanId;

    @ApiModelProperty("任务编码")
    @Column
    private String taskCode;

    @ApiModelProperty("计划类型（1--通用计划；2--自定义计划）")
    @Column
    private Integer taskType;

    @ApiModelProperty("流程编码")
    @Column
    private String flowCode;

    @ApiModelProperty("执行周期")
    @Column
    private String execCycle;

    @ApiModelProperty("执行区时")
    @Column
    private String execAreaTime;

    @ApiModelProperty("执行方式：（1--连续时间区间执行；2--固定时间执行）")
    @Column
    private Integer execStyle;

    @ApiModelProperty("执行顺序")
    @Column
    private Integer execOrder;

    @ApiModelProperty("状态1：启用，2：停用")
    @Column
    private Integer status;

    @ApiModelProperty("备注")
    @Column
    private String comment;

    @ApiModelProperty("编辑人id")
    @Column
    private Integer editId;

    @ApiModelProperty("编辑时间")
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date editTime;

    @ApiModelProperty("执行计划")
    @Transient
    private String schedule;
}
