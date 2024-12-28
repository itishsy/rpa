package com.seebon.rpa.entity.po.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PolicyWebPaidInFieldsDispose {

    private Integer sort;

    @ApiModelProperty("是否展示  1--展示  2--不展示")
    private Integer requireShow;

    /*本地字段*/
    private String localField;
}
