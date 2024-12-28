package com.seebon.rpa.entity.auth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 20:11:19
 */
@ApiModel(value ="菜单按钮")
@Table(name = "sys_button")
@Data
public class SysButton  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("按钮id")
    private Integer id;

    @Column
    @ApiModelProperty("按钮名称")
    private String name;

    @Column
    @ApiModelProperty("按钮code")
    private String code;

    @Column
    @ApiModelProperty("按钮url")
    private String url;

    @Column
    @ApiModelProperty("按钮类型 1：按钮，2：子页面")
    private Integer type;

    @Column
    @ApiModelProperty("菜单id")
    private Integer menuId;

}
