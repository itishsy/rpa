package com.seebon.rpa.entity.saas.po.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ZhenShijun
 * @dateTime 2023-03-27 13:51:36
 */
@ApiModel("客户名册统计历史表")
@Data
@Table(name = "customer_insured_register")
public class CustomerInsuredRegister implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("参保城市id")
    @Column
    private Integer addrId;

    @ApiModelProperty("参保城市")
    @Column
    private String addrName;

    @ApiModelProperty("业务类型 1：社保，2：公积金")
    @Column
    private Integer businessType;

    @ApiModelProperty("单位社保号/公积金号")
    @Column
    private String accountNumber;

    @ApiModelProperty("系统类型")
    @Column
    private String tplTypeCode;

    @ApiModelProperty("名册月份")
    @Column
    private Date dataMonth;

    @ApiModelProperty("名册人数")
    @Column
    private Integer registerNumber;

    @ApiModelProperty("创建人id")
    @Column
    private Integer createId;

    @ApiModelProperty("创建时间")
    @Column
    private Date createTime;

}
