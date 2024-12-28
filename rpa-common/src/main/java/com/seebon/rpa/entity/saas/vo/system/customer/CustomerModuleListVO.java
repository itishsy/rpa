package com.seebon.rpa.entity.saas.vo.system.customer;

import com.seebon.rpa.entity.saas.po.declare.customer.CustomerModuleList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("客户服务模块VO")
@Data
public class CustomerModuleListVO extends CustomerModuleList {

    @ApiModelProperty("服务模块名称")
    private String moduleName;

}
