package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 机器人执行监控
 */
@Data
@Table(name = "robot_execution_monitor")
@ApiModel(value = "机器人执行监控")
public class RobotExecutionMonitor {
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
    @ApiModelProperty(value = "申报人数")
    private Integer declareNum;

    @Column
    @ApiModelProperty(value = "成功人数")
    private Integer successNum;

    @Column
    @ApiModelProperty(value = "增员人数")
    private Integer zyNum;

    @Column
    @ApiModelProperty(value = "减员人数")
    private Integer jyNum;

    @Column
    @ApiModelProperty(value = "调基人数")
    private Integer tjNum;

    @Column
    @ApiModelProperty(value = "补缴人数")
    private Integer bjNum;

    @Column
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column
    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Column
    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;
}
