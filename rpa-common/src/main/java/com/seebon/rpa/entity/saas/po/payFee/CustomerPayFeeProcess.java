package com.seebon.rpa.entity.saas.po.payFee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("客户缴费流水信息PO")
@Table(name = "customer_pay_fee_process")
@Data
public class CustomerPayFeeProcess implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    protected Integer id;

    @ApiModelProperty("对应表customer_pay_fee_list uuid值")
    @Column
    private String payUuid;

    @ApiModelProperty("流水类型，0：创建，1：核定，2：缴费，3：作废")
    @Column
    private Integer processType;

    @ApiModelProperty("创建人id")
    @Column
    private Integer createId;

    @ApiModelProperty("创建时间")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
