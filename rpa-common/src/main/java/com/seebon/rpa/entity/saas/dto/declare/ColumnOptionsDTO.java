package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-09-27 18:38:39
 */
@ApiModel("字段可选范围DTO")
@Data
public class ColumnOptionsDTO {

    @ApiModelProperty("选项值")
    private String optionText;

}
