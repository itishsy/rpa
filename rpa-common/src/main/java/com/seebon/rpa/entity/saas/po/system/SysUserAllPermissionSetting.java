package com.seebon.rpa.entity.saas.po.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("运营用户全部权限配置信息")
@Table(name = "sys_user_all_permission_setting")
@Data
public class SysUserAllPermissionSetting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    private Integer id;

    @Column
    @ApiModelProperty("用户id")
    private Integer userId;

    @Column
    @ApiModelProperty("所有客户权限，1：是，0：否")
    private Integer allCust;

    @Column
    @ApiModelProperty("所有城市权限，1：是，0：否")
    private Integer allCity;

}
