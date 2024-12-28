package com.seebon.rpa.entity.saas.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-09-27 16:56:56
 */
@ApiModel("申报类型DTO")
@Data
public class DeclareTypeDTO {

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("申报类型名称")
    private String declareTypeName;

}
