package com.seebon.rpa.entity.robot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class EmployeeChangeTrackDTO {

    /**任务code*/
    @ApiModelProperty("任务code")
    private String taskCode;

    /**地址id*/
    @ApiModelProperty("地址id")
    private Integer addrId;

    /**地址名称*/
    @ApiModelProperty("地址名称")
    private String addrName;

    /**业务类型 1社保 2公积金*/
    @ApiModelProperty("业务类型")
    private Integer businessType;

    /**操作类型 1增员 2减员 3调基 5补缴*/
    @ApiModelProperty("操作类型")
    private Integer declareType;

    /**客户id*/
    @ApiModelProperty("客户id")
    private Integer clientId;

    /**申报账号*/
    @ApiModelProperty("申报账号")
    private String declareAccount;

    /**申报状态*/
    @ApiModelProperty("申报状态")
    private List<String> operationTypes;

    /**申报状态*/
    @ApiModelProperty("申报状态")
    private Integer declareStatus;

    /**增减员uuid*/
    @ApiModelProperty("增减员uuid")
    private String uuid;

    @ApiModelProperty
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    @ApiModelProperty("增减员失败原因")
    private String failReason;
}
