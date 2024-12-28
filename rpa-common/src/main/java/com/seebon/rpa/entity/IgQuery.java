package com.seebon.rpa.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IgQuery {
    @ApiModelProperty(name = "property", value = "属性值")
    private String property;

    @ApiModelProperty(name = "value", value = "值")
    private Object value;

    public IgQuery() {
    }

    public IgQuery(String property, Object value) {
        this.property = property;
        this.value = value;
    }
}
