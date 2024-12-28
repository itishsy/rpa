package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


/**
 * 机器人执行记录
 */
@Data
@Document(collection = "robot_execution")
@ApiModel(value = "机器人执行记录")
public class RobotExecutionMo {
    @Id
    @ApiModelProperty(value = "主键Id")
    private String id;

    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "异常信息")
    private String error;

    @ApiModelProperty(value = "同步时间")
    private String syncTime;

    @ApiModelProperty(value = "申报文件集合")
    private List<RobotExecutionFileInfo> reportFile;

    @ApiModelProperty(value = "执行计划")
    private String executePlan;

    @ApiModelProperty(value = "申报账户")
    private String accountNumber;
}
