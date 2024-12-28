package com.seebon.rpa.entity.auth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 18:59:16
 */
@ApiModel("用户角色PO")
@Table(name = "sys_user_role")
@Data
public class SysUserRole {

    @ApiModelProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("用户id")
    @Column
    private Integer userId;

    @ApiModelProperty("角色id")
    @Column
    private Integer roleId;

}
