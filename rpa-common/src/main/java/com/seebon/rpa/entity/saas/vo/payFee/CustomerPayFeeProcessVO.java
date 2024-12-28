package com.seebon.rpa.entity.saas.vo.payFee;

import com.seebon.rpa.entity.saas.po.payFee.CustomerPayFeeProcess;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("客户缴费流水信息VO")
@Data
public class CustomerPayFeeProcessVO extends CustomerPayFeeProcess {

    @ApiModelProperty("流水类型名称")
    private String processTypeName;

    @ApiModelProperty("操作人名称")
    private String createName;

}
