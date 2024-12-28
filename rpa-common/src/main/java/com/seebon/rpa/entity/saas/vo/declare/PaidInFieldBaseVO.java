package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PaidInFieldBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel("费用信息设置VO")
@Data
public class PaidInFieldBaseVO extends PaidInFieldBase {

    @ApiModelProperty("参保城市名称")
    private String addressName;

    @ApiModelProperty("关联模版数")
    private Integer tempCount;

}
