package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 客户端控制台-执行明细
 */
@Data
@Table(name = "robot_execution_detail")
@ApiModel(value = "机器人执行明细")
public class RobotExecutionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @Column
    @ApiModelProperty(value = "流程code")
    private String flowCode;

    @Column
    @ApiModelProperty(value = "步骤编码")
    private String stepCode;

    @Column
    @ApiModelProperty(value = "步骤名称")
    private String stepName;

    @Column
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Column
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column
    @ApiModelProperty(value = "异常信息")
    private String error;

    @Column
    @ApiModelProperty(value = "异常信息堆栈")
    private String errorStack;

    @Column
    @ApiModelProperty(value = "步骤数据")
    private String stepData;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date syncTime;
}
