package com.seebon.rpa.entity.saas.dto.system;

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
     * 子菜单
     */
    @ApiModelProperty("子菜单")
    private List<ResourceTreeDTO> childTrees;
}
