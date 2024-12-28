package com.seebon.rpa.entity.saas.po.declare.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("客户战区城市划分")
@Data
@Table(name = "customer_addr_group")
public class CustomerAddrGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("战区城市划分类型，1：指定城市，2：除其他战区外的所有城市")
    @Column
    private Integer type;

    @ApiModelProperty("战区名称")
    @Column
    private String groupName;

    @ApiModelProperty("战区下的城市，多个用逗号隔开")
    @Column
    private String addrNames;

}
