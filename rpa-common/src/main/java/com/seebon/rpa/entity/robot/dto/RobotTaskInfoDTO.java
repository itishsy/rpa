package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("客户应用列表-账户情况")
@Data
public class RobotTaskInfoDTO implements Serializable {

    @ApiModelProperty("应用编号")
    private String appCode;

    @ApiModelProperty("应用参数")
    private String appArgs;

    @ApiModelProperty("设备编码")
    private String machineCode;

    @ApiModelProperty("任务编号")
    private String taskCode;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("执行状态")
    private String taskStatus;

    @ApiModelProperty("任务状态，1：启用，0：停用")
    private Integer status;

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("单位公积金号或单位社保号")
    private String accountNumber;

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

    @ApiModelProperty("主流程数")
    private Integer flowCount;

    @ApiModelProperty("在服或启用数")
    private Integer taskStartCount;

    @ApiModelProperty("今日执行次数")
    private Integer execCount;

    @ApiModelProperty("是否存在自定义执行计划流程")
    private String haveCustom;

    @ApiModelProperty("usb端口")
    private String usbPort;

    @ApiModelProperty("原因备注")
    private String comment;

    @ApiModelProperty("操作人姓名")
    private String editName;

    @ApiModelProperty("流程计划列表")
    private List<RobotTaskScheduleDTO> scheduleList;

}
