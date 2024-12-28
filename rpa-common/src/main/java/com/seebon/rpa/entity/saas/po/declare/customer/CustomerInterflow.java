package com.seebon.rpa.entity.saas.po.declare.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author hao
 * @Date 2022/6/23 15:49
 * @Version 1.0
 **/
@Data
@Entity
@Table(name = "customer_interflow")
@ApiModel("用户问题答案表")
public class CustomerInterflow implements Serializable {

    private static final long serialVersionUID = -2207650051146389068L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty("问题")
    private String question;

    @Column
    @ApiModelProperty("答案")
    private String answer;


}
