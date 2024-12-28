package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lsh
 * @dateTime 2022-04-28
 */
@Data
public class InsuranceTypeDTO {

    @ApiModelProperty("险种名字")
    private String name;

    @ApiModelProperty("险种编码")
    private String value;
}
