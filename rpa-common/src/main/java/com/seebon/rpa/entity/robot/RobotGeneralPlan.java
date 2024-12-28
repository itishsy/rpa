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
 * @Date 2022/9/29 17:28
 * @Version 1.0
 **/

@Data
@ApiModel("通用计划表")
@Table(name = "robot_app_general_plan")
public class RobotGeneralPlan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String appCode;

    @Column
    @ApiModelProperty("执行周期")
    private String execCycle;

    @Column
    @ApiModelProperty("执行区时")
    private String execAreaTime;

    @Column
    @ApiModelProperty("执行方式：（1--连续时间区间执行；2--固定时间执行）")
    private Integer execStyle;

    @Column
    @ApiModelProperty("编辑时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date editTime;
}
