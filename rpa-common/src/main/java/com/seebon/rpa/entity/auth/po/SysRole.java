package com.seebon.rpa.entity.auth.po;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("系统角色PO")
@Table(name = "sys_role")
@Data
public class SysRole extends Identity {

    @ApiModelProperty("角色名称")
    @Column
    private String name;

    @ApiModelProperty("角色code")
    @Column
    private String code;

    @ApiModelProperty("角色状态 1：启用，0：禁用")
    @Column
    private Integer status;

    @ApiModelProperty("客户id")
    @Column
    private Integer custId;

    @ApiModelProperty("角色类型，1：运营类型角色，2：客户类型角色")
    @Column
    private Integer roleType;

    @ApiModelProperty("是否客户管理员角色，1：是，0：否")
    @Column
    private Integer mainRole;

    @ApiModelProperty("角色描述")
    @Column
    private String comment;

}
