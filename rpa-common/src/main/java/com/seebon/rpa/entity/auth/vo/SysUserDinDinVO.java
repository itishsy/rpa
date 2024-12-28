package com.seebon.rpa.entity.auth.vo;

import com.seebon.rpa.entity.auth.po.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class SysUserDinDinVO extends SysUser {


    @Column
    @ApiModelProperty(value="用户钉钉绑定手机号码")
    private String dindinPhoneNumber;
}
