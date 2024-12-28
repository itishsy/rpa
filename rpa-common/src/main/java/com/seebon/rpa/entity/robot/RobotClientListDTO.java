package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel("驾驶舱-机器人列表")
@Data
public class RobotClientListDTO implements Serializable {

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "设备编号")
    private String machineCode;

    @ApiModelProperty(value = "设备厂商")
    private String machineFactory;

    @ApiModelProperty(value = "设备状态编码")
    private Integer status;

    @ApiModelProperty(value = "设备状态名称")
    private String statusName;

    @ApiModelProperty(value = "设备总离线时长-分钟")
    private String offlineTotalTime;

    @ApiModelProperty(value = "设备月离线时长-分钟")
    private String monthOfflineTime;

    @ApiModelProperty(value = "申报账户")
    private String accountNumber;

    @ApiModelProperty(value = "执行事项")
    private String queueItemName;

    @ApiModelProperty(value = "开始时间")
    private Date praStartTime;

    @ApiModelProperty(value = "预计完成时间")
    private Date preEndTime;

    @ApiModelProperty(value = "预计耗时-分钟")
    private Integer preTime;

}
