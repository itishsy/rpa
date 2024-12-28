package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("服务项目流程信息")
@Data
public class RobotTaskServiceItemDTO implements Serializable {

    @ApiModelProperty("客户id")
    private Integer clientId;

    @ApiModelProperty("应用编号")
    private String appCode;

    @ApiModelProperty("任务编号")
    private String taskCode;

    @ApiModelProperty("设备编码")
    private String machineCode;

    @ApiModelProperty("服务项目编码")
    private Integer serviceItem;

    @ApiModelProperty("服务项目名称")
    private String serviceItemName;

    @ApiModelProperty("执行顺序")
    private Integer index;

    @ApiModelProperty("本月已完成")
    private Integer declaredCount;

    @ApiModelProperty("今日已完成")
    private Integer todayDeclaredCount;

    @ApiModelProperty("本月未完成")
    private Integer noDeclaredCount;

    @ApiModelProperty("本月网站审核中")
    private Integer auditCount;

    @ApiModelProperty("执行计划")
    private String execPlant;

    @ApiModelProperty("今日执行次数")
    private Integer execCount;

    @ApiModelProperty("流程状态，1：启用，0：停用")
    private Integer status;

    @ApiModelProperty("停用原因")
    private String comment;

    @ApiModelProperty("对用流程编号")
    private List<String> flowCodes;

    private String flows;

}
