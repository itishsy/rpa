package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-11 11:55:31
 */
@ApiModel("产品方案险种信息")
@Data
@Table(name = "policy_product_item")
public class PolicyProductItem extends Identity {

    @Column
    @ApiModelProperty("产品方案id 关联表policy_product id")
    private Integer productId;

    @Column
    @ApiModelProperty("险种code 对应字典值")
    private String itemCode;

    @Column
    @ApiModelProperty("险种名称")
    private String itemName;

}
