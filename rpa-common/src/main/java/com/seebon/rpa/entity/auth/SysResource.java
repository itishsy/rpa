package com.seebon.rpa.entity.auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysResource implements Serializable {

    @ApiModelProperty(value = "id")
    private long id;

    @ApiModelProperty(value = "uri_pattern")
    private String uriPattern;

    @ApiModelProperty(value = "common")
    private String common;

}
