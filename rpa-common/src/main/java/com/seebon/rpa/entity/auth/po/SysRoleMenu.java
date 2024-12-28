package com.seebon.rpa.entity.auth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 20:16:30
 */
@ApiModel("角色菜单PO")
@Table(name = "sys_role_menu")
@Data
public class SysRoleMenu {

    @ApiModelProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("角色id")
    @Column
    private Integer roleId;

    @ApiModelProperty("菜单id")
    @Column
    private Integer menuId;

}
