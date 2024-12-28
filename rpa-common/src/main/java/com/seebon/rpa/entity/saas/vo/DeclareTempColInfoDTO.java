package com.seebon.rpa.entity.saas.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeclareTempColInfoDTO implements Serializable {

    @ApiModelProperty("字段名称")
    private String fieldName;

    @ApiModelProperty("在excel中的列索引")
    private Integer colIndex;

    @ApiModelProperty("字段字典值")
    private String fieldCode;

}
