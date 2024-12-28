package com.seebon.rpa.entity.saas.po.declare.account;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-11 18:21:46
 */
@ApiModel("申报账户基本信息")
@Data
@Table(name = "declare_account_base")
public class DeclareAccountBase extends Identity {

    @Column
    @ApiModelProperty("客户id")
    private Integer custId;

    @Column
    @ApiModelProperty("组织名称")
    private String orgName;

    @Column
    @ApiModelProperty("纳税人识别号")
    private String taxNumber;

    @Column
    @ApiModelProperty("参保地id")
    private Integer addressId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addressName;

    @Column
    @ApiModelProperty("是否虚拟账户，1：是，0：否")
    private Integer virtually;

}
