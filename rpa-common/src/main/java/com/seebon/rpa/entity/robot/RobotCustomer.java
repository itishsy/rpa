package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author hao
 * @Date 2022/10/17 16:06
 * @Version 1.0
 **/
@Data
@Table(name = "robot_customer")
@Deprecated
public class RobotCustomer {


    @Id
    private Integer id;

    @Column
    @ApiModelProperty("客户名")
    private String customerName;

    @Column
    @ApiModelProperty("平台名")
    private String platform;

    @Column
    @ApiModelProperty("客户唯一编码")
    private String customerCode;
}
