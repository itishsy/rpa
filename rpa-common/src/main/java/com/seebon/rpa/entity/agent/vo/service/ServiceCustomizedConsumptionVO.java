package com.seebon.rpa.entity.agent.vo.service;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("定制服务消费信息VO")
@Data
public class ServiceCustomizedConsumptionVO {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("客户id")
    private Integer customerId;

    @ApiModelProperty("单位申报号/公积金号")
    private String accountNumber;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("服务编码")
    private String serviceCode;

    @ApiModelProperty("'一级定制代码'")
    private String firstCode;

    @ApiModelProperty("'定制代码'")
    private String secondCode;

    @ApiModelProperty("适用范围 1：该户全部数据，2：标定的数据")
    private Integer dataRange;

    @ApiModelProperty("适配报盘字段")
    private String adaptFieldName;

    @ApiModelProperty("定制内容")
    private String content;

    @ApiModelProperty("提示信息")
    private String promptInfo;
}
