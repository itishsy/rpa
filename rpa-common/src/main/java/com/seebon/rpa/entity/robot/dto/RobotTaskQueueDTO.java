package com.seebon.rpa.entity.robot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@ApiModel("机器人运行队列DTO")
@Data
public class RobotTaskQueueDTO implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("任务编码")
    private String taskCode;

    @ApiModelProperty("应用参数信息")
    private String appArgs;

    @ApiModelProperty("机器人编码")
    private String machineCode;

    @ApiModelProperty("机器人编码")
    private String machineName;

    @ApiModelProperty(value = "运行状态  0-未执行  1-执行中 2-已执行")
    private Integer status;

    @ApiModelProperty(value = "运行状态名称")
    private String statusName;

    @ApiModelProperty(value = "申报单位")
    private String companyName;

    @ApiModelProperty(value = "申报账户")
    private String accountNumber;

    @ApiModelProperty(value = "任务发布者")
    private String headName;

    @ApiModelProperty(value = "事项")
    private String queueItemName;

    @ApiModelProperty(value = "执行优先级排序")
    private Integer sort;

    @ApiModelProperty(value = "发布时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "预计完成时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preEndTime;

    @ApiModelProperty(value = "总记录数")
    private Integer totalData;

    @ApiModelProperty(value = "完成数")
    private Integer finishData = 0;

}
