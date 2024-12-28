package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "social_shanghai_area")
@ApiModel("上海地址信息")
@Data
public class SocialShanghaiArea {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("地址层级，1联系地址区县、2联系街道或路、3联系弄或号")
    @Column
    private Integer level;

    @ApiModelProperty("编码")
    @Column
    private String code;

    @ApiModelProperty("地区")
    @Column
    private String addr;

    @ApiModelProperty("父节点  定级节点的父节点为0")
    @Column
    private Integer parentId;

    @ApiModelProperty("顺序")
    @Column
    private Integer idx;

    @ApiModelProperty("创建时间")
    @Column
    private Date createTime;

    @ApiModelProperty("描述")
    @Column
    private String comment;
}
