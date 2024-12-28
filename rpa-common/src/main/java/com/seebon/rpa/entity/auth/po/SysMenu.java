package com.seebon.rpa.entity.auth.po;


import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhengYuan
 * @dateTime 2022-04-19 14:07:00
 */
@Data
@Table(name = "sys_menu")
public class SysMenu extends Identity {

    /**
     * 菜单名
     */
    @Column
    @ApiModelProperty(value = "菜单名")
    private String name;

    /**
     * 菜单的url
     */
    @Column
    @ApiModelProperty(value = "菜单url")
    private String url;

    /**
     * 父级菜单id
     */
    @Column
    @ApiModelProperty(value = "父级菜单id")
    private Integer parentId;

    /**
     * 菜单显示顺序
     */
    @Column
    @ApiModelProperty(value = "菜单显示顺序")
    private Integer sort;

    /**
     * 菜单层级
     */
    @Column
    @ApiModelProperty(value = "菜单层级")
    private Integer level;

    /**
     * 菜单图标
     */
    @Column
    @ApiModelProperty(value = "菜单图标")
    private String iconClass;


    /**
     * 菜单状态
     */
    @Column
    @ApiModelProperty(value = "菜单状态，1:启用，0：停用")
    private Integer status;


    /**
     * 备注
     */
    @Column
    @ApiModelProperty(value = "备注")
    private String comment;


    /**
     * 菜单类别
     */
    @Column
    @ApiModelProperty(value = "菜单类别，1：运营后台菜单，2：客户端菜单")
    private Integer menuType;


    @Column
    @ApiModelProperty(value = "服务项目")
    private String moduleCode;

    @Column
    @ApiModelProperty(value = "服务项目名称")
    private String moduleName;

}
