package com.seebon.rpa.entity.saas.po.declare.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("客户服务模块PO")
@Table(name = "customer_module_list")
@Data
public class CustomerModuleList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("服务模块")
    @Column
    private String moduleCode;

}
