package com.seebon.rpa.entity.auth.vo;

import com.seebon.rpa.entity.auth.po.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 19:44:13
 */
@Data
@ApiModel("")
public class RoleResourceVO extends SysMenu {

    @ApiModelProperty("true表示角色已分配该菜单，false表示角色未分配该菜单")
    private boolean checked;

    private String perId;

    @ApiModelProperty("菜单类型 1：菜单，2：按钮")
    private Integer perType;

    @ApiModelProperty("子菜单")
    private List<RoleResourceVO> subRoleResourceVOS;

}
