package com.seebon.rpa.entity.saas.po.service;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("定制服务基础信息")
@Data
@Table(name = "service_customized_base")
public class ServiceCustomizedBase {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("一级定制代码")
    @Column
    private String firstCode;

    @ApiModelProperty("定制代码")
    @Column
    private String secondCode;

    @ApiModelProperty("服务代码")
    @Column
    private String serviceCode;

    @ApiModelProperty("适配报盘字段")
    @Column
    private String adaptFieldName;

    @ApiModelProperty("参保城市id")
    @Column
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    @Column
    private String addrName;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    @Column
    private Integer businessType;

    @ApiModelProperty("适用范围 1：该户全部数据，2：标定的数据")
    @Column
    private Integer dataRange;

    @ApiModelProperty("服务板块")
    @Column
    private String serviceBlockCode;

    @ApiModelProperty("定制内容")
    @Column
    private String content;

    @ApiModelProperty("提示信息")
    @Column
    private String promptInfo;

    @ApiModelProperty("备注")
    @Column
    private String comments;

    @ApiModelProperty("异常信息反馈")
    @Column
    private String errorMessage;

}
