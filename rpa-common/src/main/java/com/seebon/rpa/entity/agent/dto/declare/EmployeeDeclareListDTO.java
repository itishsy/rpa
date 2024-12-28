package com.seebon.rpa.entity.agent.dto.declare;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeItemBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EmployeeDeclareListDTO implements Serializable {

    @ApiModelProperty("增减员id")
    private Integer id;

    @ApiModelProperty("增减员uuid")
    private String uuid;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("单位社保号 or 单位公积金号")
    private String accountNumber;

    @ApiModelProperty("申报状态")
    private Integer declareStatus;

    @ApiModelProperty("补充公积金申报状态")
    private Integer suppDeclareStatus;

    @ApiModelProperty("申报类型")
    private String declareType;

    @ApiModelProperty("失败原因")
    private String failCause;

    @ApiModelProperty("补充公积金失败原因")
    private String suppFailCause;

    @ApiModelProperty("人员姓名")
    private String name;

    @ApiModelProperty("人员证件号")
    private String idCard;

    @ApiModelProperty("最近的机器人执行批次号")
    private String lastRobotExecCode;

    @ApiModelProperty("险种信息")
    private List<EmployeeSocialDeclareChangeItemBase> items;


}
