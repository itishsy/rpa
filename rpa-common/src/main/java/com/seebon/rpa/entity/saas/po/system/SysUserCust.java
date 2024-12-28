package com.seebon.rpa.entity.saas.po.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("运营后台用户负责客户信息po")
@Table(name = "sys_user_cust")
@Data
public class SysUserCust implements Serializable {

    @Id
    @ApiModelProperty(value="主键id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value="用户id")
    private Integer userId;

    @Column
    @ApiModelProperty(value="客户id")
    private Integer custId;

}
