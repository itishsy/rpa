package com.seebon.rpa.entity.robot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value = "客户机器人指令")
public class RobotCommandDTO {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "任务编码")
    private String taskCode;

    @ApiModelProperty(value = "执行参数")
    private String args;

    @ApiModelProperty(value = "指令 start/stop/run")
    private String command;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "同步状态  0-未同步  1-已同步")
    private Integer status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
