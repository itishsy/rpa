package com.seebon.rpa.entity.robot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class RobotTaskScheduleDTO implements Serializable {

    @ApiModelProperty("客户id")
    private Integer clientId;

    @ApiModelProperty(name = "应用编号", required = true)
    private String appCode;

    @ApiModelProperty("应用参数")
    private String appArgs;

    @ApiModelProperty(name = "任务编号", required = true)
    private String taskCode;

    @ApiModelProperty("设备编码")
    private String machineCode;

    @ApiModelProperty(name = "流程编号", required = true)
    private String flowCode;

    @ApiModelProperty("流程名称")
    private String flowName;

    @ApiModelProperty("服务项目编码")
    private Integer serviceItem;

    @ApiModelProperty("服务项目名称")
    private String serviceItemName;

    @ApiModelProperty("执行顺序")
    private Integer execOrder;

    @ApiModelProperty("执行次数")
    private Integer execCount;

    @ApiModelProperty(name = "执行周期,自定义计划必填", required = true)
    private String execCycle;

    @ApiModelProperty(name = "执行区时，自定义计划的连续时间区间和固定时间执行必填", required = true)
    private String execAreaTime;

    @ApiModelProperty(name = "执行方式,自定义计划必填", required = true)
    private Integer execStyle;

    @ApiModelProperty("流程状态，1：启用，0：停用")
    private Integer status;

    @ApiModelProperty("停用原因")
    private String comment;

    @ApiModelProperty("执行计划")
    private String execPlant;

    @ApiModelProperty("编辑人id")
    private Integer editId;

    @ApiModelProperty("编辑人姓名")
    private String editName;

    @ApiModelProperty("编辑时间")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date editTime;

    @ApiModelProperty("流程类型，1：主流程，2：子流程")
    private String flowType;

    @ApiModelProperty(name = "计划类型，1：引用上级计划，2：自定义计划", required = true)
    private Integer taskType;

}
