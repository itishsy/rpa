package com.seebon.rpa.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IgFilter {
    @ApiModelProperty(name = "property", value = "属性")
    private String property;

    @ApiModelProperty(name = "value", value = "属性值")
    private Object value;

    @ApiModelProperty(name = "operator", value = "操作类型：like")
    private String operator;
}
