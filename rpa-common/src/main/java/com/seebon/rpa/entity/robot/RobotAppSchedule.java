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
@Table(name = "robot_app_schedule")
@ApiModel("流程执行计划")
public class RobotAppSchedule implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer generalPlanId;

    @Column
    private String flowCode;

    @ApiModelProperty("编辑人id")
    @Column
    private Integer editId;

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

    @ApiModelProperty("计划类型（1--通用计划；2--自定义计划）")
    @Column
    private Integer taskType;

    @ApiModelProperty("编辑时间")
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date editTime;

    @ApiModelProperty("执行计划")
    @Transient
    private String schedule;

}
