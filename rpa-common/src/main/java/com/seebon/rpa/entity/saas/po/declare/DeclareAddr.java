package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-21 09:40:30
 */
@Data
@ApiModel("参保城市")
@Table(name = "declare_addr")
public class DeclareAddr extends Identity{

    @Column
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addrName;

    @Column
    @ApiModelProperty("社保是否允许补缴1：是，0：否")
    private Integer socialAllowSupp;

    @Column
    @ApiModelProperty("公积金是否允许补缴1：是，0：否")
    private Integer accfundAllowSupp;

    @Column
    @ApiModelProperty("申报接口权限，1：是 0：否")
    private Boolean declareAuth;

    @Column
    @ApiModelProperty("政策接口权限，1：是 0：否")
    private Boolean policy;

    @Column
    @ApiModelProperty("计算接口权限，1：是 0：否")
    private Boolean calculate;

    @Column
    @ApiModelProperty("社保：是否允许同月增减员 1:是，0：否")
    private Integer socialAllowAgainInsure;

    @Column
    @ApiModelProperty("公积金：是否允许同月增减员 1:是，0：否")
    private Integer accfundAllowAgainInsure;
}
