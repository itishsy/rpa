package com.seebon.rpa.entity.saas.vo.system;

import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.saas.po.system.SysUserDeclareAccount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("用户申报账户权限信息")
@Data
public class SysUserDeclareAccountVO extends SysUserDeclareAccount {

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("是否选中，1：是，0否")
    private Integer checked;

    @ApiModelProperty("已分配用户")
    private List<SysUser> assignedUser;

}
