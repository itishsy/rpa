package com.seebon.rpa.entity.saas.po.social;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel("贫困和失业人员客户表")
@Data
@Table(name = "social_cost_cust")
public class SocialCostCust {
    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("客户名")
    @Column
    private String custName;

    @ApiModelProperty("销售人员")
    @Column
    private String saleName;

    @ApiModelProperty("申报状态")
    @Column
    private Integer status;

    @ApiModelProperty("创建人Id")
    @Column
    private Integer createId;

    @ApiModelProperty("创建人名称")
    @Column
    private String createName;

    @ApiModelProperty("创建时间")
    @Column
    private Date createTime;

    @ApiModelProperty("交付人员")
    @Column
    private String serviceName;

    @ApiModelProperty("退税申领金额")
    @Column
    private BigDecimal refundTaxAmt;

    @ApiModelProperty("失业申领金额")
    @Column
    private BigDecimal lostWorkAmt;

}
