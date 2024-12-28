package com.seebon.rpa.entity.saas.po.declare.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("客户方对接人信息")
@Table(name = "customer_contact_person_list")
@Data
public class CustomerContactPersonList implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;


    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("对接人姓名")
    @Column
    private String name;

    @ApiModelProperty("对接人手机号码")
    @Column
    private String phoneNumber;

    @ApiModelProperty("对接人电子邮箱")
    @Column
    private String email;

}
