package com.seebon.rpa.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



/**
 * 机器人执行记录
 */
@Data
@ApiModel(value = "机器人执行记录")
public class RobotExecutionVo{

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
