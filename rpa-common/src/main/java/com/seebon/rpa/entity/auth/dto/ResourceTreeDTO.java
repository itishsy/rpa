package com.seebon.rpa.entity.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("菜单信息-树形")
@Data
public class ResourceTreeDTO {
    /**
     * 菜单Id
     */
    @ApiModelProperty("菜单Id")
    private Integer id;
    /**
     * 菜单名称
     */
    @ApiModelProperty("菜单名称")
    private String name;
    /**
     * 菜单地址
     */
    @ApiModelProperty("菜单地址")
    private String url;
    /**
     * 父节点Id
     */
    @ApiModelProperty("父节点Id")
    private Integer parentId;
    /**
     * 菜单顺序
     */
    @ApiModelProperty("菜单顺序")
    private Integer sort;
    /**
     * 菜单深度
     */
    @ApiModelProperty("菜单深度")
    private Integer level;

    /**
     * 菜单图标Class
     */
    @ApiModelProperty("菜单图标Class")
    private String iconClass;

    /**
     * 菜单状态
     */
    @ApiModelProperty(value = "菜单状态，1:启用，0：停用")
    private Integer status;


    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String comment;


    /**
     * 菜单类别
     */
    @ApiModelProperty(value = "菜单类别，1：运营后台菜单，2：客户端菜单")
    private Integer menuType;


    @ApiModelProperty(value = "服务项目")
    private String moduleCode;

    @ApiModelProperty(value = "服务项目名称")
    private String moduleName;

    /**
     * 子菜单
     */
    @ApiModelProperty("子菜单")
    private List<ResourceTreeDTO> childTrees;
}
