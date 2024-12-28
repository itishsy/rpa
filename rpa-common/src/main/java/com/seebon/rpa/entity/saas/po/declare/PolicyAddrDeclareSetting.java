package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO
 *
 * @author zjf
 * @describe 申报政策设置
 * @date 2023/5/29 11:02
 */
@Data
@ApiModel("申报政策设置")
@Table(name = "policy_addr_declare_setting")
public class PolicyAddrDeclareSetting{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer businessType;

    @Column
    @ApiModelProperty("年度调基月")
    private Integer transferBaseMonth;

    @Column
    @ApiModelProperty("是否允许同月增减(0 否 1 是)")
    private Integer isMonthIncreaseDecrease;

    @Column
    @ApiModelProperty("增补合并申报(0 否 1 是)")
    private Integer addMergeDeclare;

    @Column
    @ApiModelProperty("可往前参保时间类型")
    private String agoInsuredMonthType;

    @Column
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Column
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @Column
    @ApiModelProperty("备注")
    private String comment;

}
