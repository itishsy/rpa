package com.seebon.rpa.entity.saas.po.declare.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ApiModel("客户合同PO")
@Table(name = "customer_contract_list")
@Data
public class CustomerContractList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;


    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("合同签订日期")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date signingDate;

    @ApiModelProperty("合同编号")
    @Column
    private String code;

    @ApiModelProperty("合同开始日期")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date beginDate;

    @ApiModelProperty("合同结束日期")
    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endDate;

}
