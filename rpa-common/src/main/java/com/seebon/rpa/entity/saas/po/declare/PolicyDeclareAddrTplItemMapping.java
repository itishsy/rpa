package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-26 13:58:23
 */
@ApiModel("地区申报模板险种信息")
@Table(name = "policy_declare_addr_tpl_item_mapping")
@Data
public class PolicyDeclareAddrTplItemMapping extends Identity {

    @Column
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addrName;

    @Column
    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer businessType;

    @Column
    @ApiModelProperty("模板类型")
    private String tplType;

    @Column
    @ApiModelProperty("险种code")
    private String itemCode;

    @Column
    @ApiModelProperty("险种名称")
    private String itemName;


}
