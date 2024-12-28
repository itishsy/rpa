package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyProduct;
import com.seebon.rpa.entity.saas.po.declare.PolicyProductItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-11 12:17:27
 */
@Data
@ApiModel("产品方案VO")
public class PolicyProductVO extends PolicyProduct {

    @ApiModelProperty("业务类型名称")
    private String bussinssTypeName;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("社保账号")
    private String socialnum;

    @ApiModelProperty("公积金账号")
    private String accfundnum;

    @ApiModelProperty("公积金:单位社保号id")
    private Integer compAccountIdAccfund;

    @ApiModelProperty("社保:单位社保号id")
    private Integer compAccountIdSocial;

    @ApiModelProperty("险种信息")
    private List<PolicyProductItem> items;

}
