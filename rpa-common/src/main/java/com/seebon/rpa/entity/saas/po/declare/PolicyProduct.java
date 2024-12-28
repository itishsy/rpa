package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-11 11:37:15
 */
@ApiModel("参保方案信息")
@Data
@Table(name = "policy_product")
public class PolicyProduct extends Identity {

    @Column
    @ApiModelProperty("方案名称")
    private String productName;

    @Column
    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer bussinssType;

    @Column
    @ApiModelProperty("参保地id")
    private Integer addressId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addressName;

    @Column
    @ApiModelProperty("状态 1：启用，0：禁用")
    private Integer status;

    @Column
    @ApiModelProperty("客户id")
    private Integer custId;

    @Column
    @ApiModelProperty("对外显示 1--显示，2--不显示")
    private Integer outDisplay;

}
