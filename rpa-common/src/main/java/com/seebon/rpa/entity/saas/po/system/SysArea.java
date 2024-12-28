package com.seebon.rpa.entity.saas.po.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("行政区划信息")
@Table(name = "sys_area")
@Data
public class SysArea implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    protected Integer id;

    @Column
    @ApiModelProperty("名称")
    private String name;

    @Column
    @ApiModelProperty("父id")
    private Integer parentid;

    @Column
    @ApiModelProperty("简称")
    private String shortname;

    @Column
    @ApiModelProperty("层级")
    private String leveltype;

    @Column
    @ApiModelProperty("城市编码")
    private String citycode;

    @Column
    @ApiModelProperty("")
    private String zipcode;

    @Column
    @ApiModelProperty("详细名称")
    private String mergername;

    @Column
    @ApiModelProperty("经度")
    private String lng;

    @Column
    @ApiModelProperty("纬度")
    private String lat;

    @Column
    @ApiModelProperty("拼音")
    private String pinyin;

    @Column
    @ApiModelProperty("拼音缩写")
    private String pinyinShort;

    @Column
    @ApiModelProperty("状态")
    private String status;

    @Column
    @ApiModelProperty("所属省份代码")
    private String provinceCode;

    @Column
    @ApiModelProperty("所属省份名称")
    private String provinceName;

}
