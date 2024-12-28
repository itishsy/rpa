package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 客户端控制台-执行明细
 */
@Data
@Document(collection = "robot_execution_detail")
@ApiModel(value = "机器人执行明细")
public class RobotExecutionDetailMo {
    @Id
    @ApiModelProperty(value = "主键Id")
    private String id;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @ApiModelProperty(value = "流程code")
    private String flowCode;

    @ApiModelProperty(value = "步骤编码")
    private String stepCode;

    @ApiModelProperty(value = "步骤名称")
    private String stepName;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "开始时间(long)")
    private Long startDate;

    @ApiModelProperty(value = "结束时间(long)")
    private Long endDate;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "步骤数据")
    private String stepData;

    @ApiModelProperty(value = "异常信息")
    private String error;

    @ApiModelProperty(value = "异常信息堆栈")
    private String errorStack;

    @ApiModelProperty(value = "同步时间")
    private String syncTime;
}
