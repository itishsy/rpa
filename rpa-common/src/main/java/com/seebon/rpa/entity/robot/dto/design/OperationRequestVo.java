package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe 运维监控请求参数
 * @date 2023/4/18 13:48
 */
@ApiModel("运维监控请求参数vo")
@Data
public class OperationRequestVo{

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "客户名称")
    private String custName;

    @ApiModelProperty(value = "预警code")
    private String warnCode;

    @ApiModelProperty(value = "申报账号")
    private String accountNumber;

    @ApiModelProperty(value = "应用参数值")
    private String appArgs;

    @ApiModelProperty(value = "设备编号")
    private String machineCode;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用名字")
    private String appName;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "执行id")
    private String executionId;

    @ApiModelProperty(value = "错误原因")
    private String errorResult;

    @ApiModelProperty(value = "流程编码")
    private String flowCode;

    @ApiModelProperty(value = "流程名字")
    private String flowName;

    @ApiModelProperty(value = "任务名字")
    private String taskName;


}
