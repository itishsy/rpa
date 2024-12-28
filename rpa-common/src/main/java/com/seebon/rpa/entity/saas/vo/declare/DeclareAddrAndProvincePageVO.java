package com.seebon.rpa.entity.saas.vo.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author maoxp
 * @dateTime 2022-04-21 09:43:21
 */
@ApiModel("参保城市分页vo")
@Data
public class DeclareAddrAndProvincePageVO {

    @Column
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addrName;

    @Column
    @ApiModelProperty("省id")
    private Integer provinceId;

    @Column
    @ApiModelProperty("省名称")
    private String provinceName;

    @Column
    @ApiModelProperty("市id")
    private Integer cityId;

    @Column
    @ApiModelProperty("市")
    private String cityName;
}
