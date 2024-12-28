package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("执行查询查询参数信息")
@Data
public class ExecutionQueryDTO implements Serializable {

    @ApiModelProperty("应用编码")
    private String appCode;

    @ApiModelProperty("执行时间起")
    private String execStartTime;

    @ApiModelProperty("执行时间止")
    private String execEndTime;

    @ApiModelProperty("具体时段起")
    private String execTimeStart;

    @ApiModelProperty("具体时段止")
    private String execTimeEnd;

    @ApiModelProperty("客户id")
    private Integer clientId;

    @ApiModelProperty("设备编号")
    private String machineCode;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("流程名称")
    private String flowName;

    @ApiModelProperty("流程编码")
    private List<String> flowCodes;

    @ApiModelProperty("任务编码-申报账户")
    private String taskCode;

    @ApiModelProperty("状态")
    private List<Integer> statuses;

    @ApiModelProperty(value = "执行编码")
    private String executionCode;
}
