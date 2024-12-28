package com.seebon.rpa.entity.robot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.RobotTask;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.RobotTaskNotice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Author hao
 * @Date 2022/11/18 18:31
 * @Version 1.0
 **/
@Data
public class RobotTaskGenerateQueueVO {


    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "执行计划")
    private String schedule;

    @ApiModelProperty(value = "状态  0-暂停  1-启动")
    private Integer status;

    @ApiModelProperty(value = "运行标识  0-未执行  1-执行中")
    private Integer run;

    @ApiModelProperty(value = "同步标识  0-未同步  1-已同步")
    private Integer sync;

    @ApiModelProperty(value = "费用机器同步标识  0-未同步  1-已同步")
    private Integer syncFee;

    @ApiModelProperty(value = "同步机器标识码，逗号隔开")
    private String syncMachineCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "编辑人id")
    private String editName;

    @ApiModelProperty(value = "执行周期")
    private String  execCycle;

    @ApiModelProperty(value = "执行区时")
    private String  execAreaTime;

    @ApiModelProperty(value = "执行方式：（1--连续时间区间执行；2--固定时间执行）")
    private Integer execStyle;

    @ApiModelProperty(value = "计划类型：（1--通用计划；2--自定义计划")
    private Integer taskType;

    private Integer execOrder;

    @ApiModelProperty(value = "最近执行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastExecutionTime;

    @ApiModelProperty(value = "启用时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date enableTime;

    @ApiModelProperty(value = "同步时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @ApiModelProperty(value = "备注")
    private String comment;

    /**
     * 单位名称
     */
    private String declareAccount;

    private Integer valueType;

    /**
     * 待申报人数
     */
    private Integer awaitDeclareNumber;

    private Date lastExecTime;

    /**
     * 单位社保号/公积金号
     */
    private String accountNumber;

    /**
     * 业务类型 社保/公积金
     */
    private String businessType;

    @ApiModelProperty("上次已完成申报人数")
    private Integer lastCompletedCount;

    private String statusName;

    private String usbPort;

    private RobotTaskNotice robotTaskNotice;

    private String appArgs;

    private String orgName;

    private String appName;

    private Integer fixMachine;

    private String executionCode;

    private List<RobotTaskArgs> taskArgsList;
}
