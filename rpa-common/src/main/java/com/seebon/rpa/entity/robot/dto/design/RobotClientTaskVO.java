package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("机器人任务")
@Data
public class RobotClientTaskVO {

    /**
     * 应用编码
     */
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    /**
     * 任务编码
     */
    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 执行计划
     */
    @ApiModelProperty(value = "执行计划")
    private String schedule;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 流程编码
     */
    @ApiModelProperty(value = "流程编码")
    private List<String> flowCodes;

    /**
     * 任务参数
     */
    @ApiModelProperty(value = "任务参数，json")
    private String argsJson;

}
