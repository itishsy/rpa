package com.seebon.rpa.entity.saas.dto.system;

import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("用户申报账户权限DTO")
@Data
public class SysUserDeclareAccountDTO implements Serializable {

    @ApiModelProperty("参保城市数")
    private Integer addrCount = 0;

    @ApiModelProperty("申报单位数")
    private Integer orgCount = 0;

    @ApiModelProperty("申报账户数")
    private Integer decalreAccountCount = 0;

    @ApiModelProperty("申报账户列表")
    private List<SysUserDeclareAccountVO> userDeclareAccountVOList;

}
