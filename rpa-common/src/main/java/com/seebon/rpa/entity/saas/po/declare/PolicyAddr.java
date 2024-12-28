package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-19 19:29:45
 */
@ApiModel("参保地信息")
@Data
@Table(name = "policy_addr")
public class PolicyAddr extends Identity {

    @Column
    @ApiModelProperty("省id")
    private Integer provinceId;

    @Column
    @ApiModelProperty("省名称")
    private String provinceName;

    @Column
    @ApiModelProperty("城市id")
    private Integer cityId;

    @Column
    @ApiModelProperty("城市名称")
    private String cityName;

    @Column
    @ApiModelProperty("参保地Id")
    private Integer addrId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addrName;

    @Column
    @ApiModelProperty("参保地拼音")
    private String pinyin;

    @Column
    @ApiModelProperty("参保地拼音缩写")
    private String pinyinShort;

    @Column
    @ApiModelProperty("是否热门地区 1：是，0：否")
    private Integer isHot;

    @Column
    @ApiModelProperty("经度")
    private String lng;

    @Column
    @ApiModelProperty("纬度")
    private String lat;
}
