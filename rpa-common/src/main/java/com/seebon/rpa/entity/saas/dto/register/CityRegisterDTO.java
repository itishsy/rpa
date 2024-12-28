package com.seebon.rpa.entity.saas.dto.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("城市在册数")
@Data
public class CityRegisterDTO implements Serializable {

    @ApiModelProperty("城市id")
    private Integer addrId;

    @ApiModelProperty("城市名称")
    private String addrName;

    @ApiModelProperty("城市在册数")
    private Integer registerNumber;

}
