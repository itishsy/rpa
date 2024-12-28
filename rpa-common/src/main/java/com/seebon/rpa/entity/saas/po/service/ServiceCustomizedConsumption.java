package com.seebon.rpa.entity.saas.po.service;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("定制服务消费信息")
@Data
@Table(name = "service_customized_consumption")
public class ServiceCustomizedConsumption {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer customerId;

    @ApiModelProperty("业务类型")
    @Column
    private Integer businessType;

    @ApiModelProperty("单位申报号/公积金号")
    @Column
    private String accountNumber;

    @ApiModelProperty("参保城市id")
    @Column
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    @Column
    private String addrName;

    @ApiModelProperty("服务编码")
    @Column
    private String serviceCode;

    @ApiModelProperty("服务状态 1--未上线  2--已上线")
    @Column
    private Integer serviceStatus;
}
