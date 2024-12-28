package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author ZhenShijun
 * @dateTime 2022-10-13 17:08:32
 */
@ApiModel("报盘字段条件明细表")
@Data
public class ColumnConditionItemDTO {

    @ApiModelProperty("字段名")
    private String fieldName;

    @ApiModelProperty("条件满足关系 1：等于，2：不等于，3：包含，4：不包含")
    private Integer contrastMode;

    @ApiModelProperty("条件对比值")
    private String contrastValue;

}
