package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author lsh
 * @dateTime 2022-04-26
 */
@Data
public class DynamicFieldDTO {

    @ApiModelProperty("动态字段字段名")
    private String name;

    @ApiModelProperty("动态字段字段值")
    private String value;

    @ApiModelProperty("区分别动态字段里是否有自定义字段，不需要页面作展示")
    private String tableCode;
}
