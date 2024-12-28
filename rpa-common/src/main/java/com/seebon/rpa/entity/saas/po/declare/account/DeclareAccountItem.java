package com.seebon.rpa.entity.saas.po.declare.account;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-11 18:25:52
 */
@ApiModel("申报账户社保、公积金账号信息")
@Data
@Table(name = "declare_account_item")
public class DeclareAccountItem extends Identity {

    @Column
    @ApiModelProperty("申报账户基本信息id 关联表declare_account_base id")
    private Integer baseId;

    @Column
    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer bussinssType;

    @Column
    @ApiModelProperty("单位社保号或公积金号")
    private String accountNumber;

    @Column
    @ApiModelProperty("'1表示启用，0表示禁用',")
    private Integer sign;

}
