package com.seebon.rpa.entity.saas.vo.payFee;

import com.seebon.rpa.entity.saas.po.declare.PolicyAddrCostSetting;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Hey man，take your time！
 *
 * @Author: 李松洋
 * @Date: Create in 9:44 2023/10/26
 */
@Data
public class PayFeePolicyVO extends PolicyAddrCostSetting {

    @ApiModelProperty("业务申报类型")
    private String businessDeclareType;

    @ApiModelProperty("截止日期")
    private String deadLine;

    @ApiModelProperty("剩余天数")
    private Integer restDay;

    @ApiModelProperty("公司名称")
    private String orgName;

}
