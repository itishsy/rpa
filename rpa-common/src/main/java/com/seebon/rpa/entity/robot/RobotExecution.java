package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人执行记录
 */
@Data
@Table(name = "robot_execution")
@ApiModel(value = "机器人执行记录")
public class RobotExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

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
    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Column
    @ApiModelProperty(value = "状态,0-执行失败，1-执行成功，2-执行中")
    private Integer status;

    @Column
    @ApiModelProperty(value = "异常信息")
    private String error;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;
}
