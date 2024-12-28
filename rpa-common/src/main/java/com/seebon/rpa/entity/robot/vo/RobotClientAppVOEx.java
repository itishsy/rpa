package com.seebon.rpa.entity.robot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@ApiModel("机器人客户端应用")
@Data
public class RobotClientAppVOEx {

    private Integer id;

    private Integer clientId;

    private String taskCode;

    @ApiModelProperty("序号")
    private Integer index;

    @ApiModelProperty("平台方")
    private String platform;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty("客户任务列表")
    private String taskName;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "版本")
    private String version;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Integer status;

    @ApiModelProperty("最新执行时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastExecutionTime;

    private String concatLastExecutionTime;

    @ApiModelProperty("状态")
    private String state;

    private Integer successTotal;

    private Integer failureTotal;

    private Integer dataNumberTotal;

    private List<RobotTaskVO> robotTaskVOS;

    @ApiModelProperty("单位社保号/公积金信息")
    private String accountNumbers;

    @ApiModelProperty("设备编码")
    private String machineCode;

    @ApiModelProperty("设备厂商")
    private String machineFactory;

    @ApiModelProperty("设备状态")
    private String machineStatus;

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("申报账户-传参用")
    private String accNumber;

    @ApiModelProperty("申报账户状态")
    private String taskStatus;

    @ApiModelProperty("申报账户停用原因")
    private String taskComment;

    @ApiModelProperty("执行计划")
    private String execPlant;

    @ApiModelProperty("是否存在自定义执行计划流程")
    private String haveCustom;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("参保城市")
    private Integer addrId;

    @ApiModelProperty("本月已完成")
    private Integer declaredCount;

    @ApiModelProperty("今日已完成")
    private Integer todayDeclaredCount;

    @ApiModelProperty("本月未完成")
    private Integer noDeclaredCount;

    @ApiModelProperty("今日执行次数")
    private Integer execCount;

    @ApiModelProperty("应用参数值")
    private String appArgs;

    @ApiModelProperty("网站审核中")
    private Integer auditCount;
}
