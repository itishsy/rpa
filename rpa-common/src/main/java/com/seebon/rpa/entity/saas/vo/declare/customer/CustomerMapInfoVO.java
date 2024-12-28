package com.seebon.rpa.entity.saas.vo.declare.customer;

import com.seebon.rpa.entity.saas.po.declare.customer.CustomerMapInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("驾驶餐标题、中心城市配置信息VO")
@Data
public class CustomerMapInfoVO extends CustomerMapInfo {

    @ApiModelProperty("经度")
    private String lng;

    @ApiModelProperty("纬度")
    private String lat;

    @ApiModelProperty("图标访问地址")
    private String logoFileUrl;

}
