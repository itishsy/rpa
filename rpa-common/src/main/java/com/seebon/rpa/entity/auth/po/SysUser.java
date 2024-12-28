package com.seebon.rpa.entity.auth.po;


import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel(value="com-seebon-rpa-entity-po-system-SysUser")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_user")
public class SysUser extends Identity {

    @Column
    @ApiModelProperty(value="用户名")
    private String username;

    @Column
    @ApiModelProperty(value="")
    private String clientId;

    @Column
    @ApiModelProperty(value="")
    private String password;

    /**
     * 客户id 关联表customer_base id
     */
    @Column
    @ApiModelProperty(value="客户id 关联表customer_base id")
    private Integer custId;

    @Column
    @ApiModelProperty(value="客户名称")
    private String custName;


    @Column
    @ApiModelProperty(value="账户类型，1：运营用户，2：客户用户")
    private Integer userType;

    @Column
    @ApiModelProperty(value="是否客户主账号，1：是，0：否")
    private Integer mainUser;

    @Column
    @ApiModelProperty(value="用户类别，1：正常用户，2：机器人盒子账户")
    private Integer userCategory;

    @Column
    @ApiModelProperty(value="")
    private String salt;

    @Column
    @ApiModelProperty(value="用户手机号码")
    private String phoneNumber;


    @Column
    @ApiModelProperty(value="用户电子邮箱")
    private String email;


    @Column
    @ApiModelProperty(value="用户姓名")
    private String name;

    @Column
    @ApiModelProperty(value="账号状态 1：已激活，0：已注销")
    private Integer status;

    @Column
    @ApiModelProperty(value="登录session维持时间，单位小时")
    private Integer expiresIn;

    @Column
    @ApiModelProperty(value="备注")
    private String comment;

    public String[] spiltEmail(){
        return this.getEmail() == null ? null : this.getEmail().split(",");
    }

    private static final long serialVersionUID = 1L;

}