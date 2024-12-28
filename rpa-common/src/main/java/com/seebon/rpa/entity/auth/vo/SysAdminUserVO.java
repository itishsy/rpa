package com.seebon.rpa.entity.auth.vo;

import com.seebon.rpa.entity.auth.po.SysRole;
import com.seebon.rpa.entity.auth.po.SysUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("运营后台用户vo")
@Data
public class SysAdminUserVO extends SysUser {

    @ApiModelProperty("负责客户数")
    private String customerNumber;

    @ApiModelProperty("负责城市数")
    private String cityNumber;

    @ApiModelProperty("社保申报账户数")
    private String socialAccountNumber;

    @ApiModelProperty("公积金申报账户数")
    private String accfundAccountNumber;

    @ApiModelProperty("分配角色")
    private List<SysRole> roles;

}
