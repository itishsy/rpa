package com.seebon.rpa.entity.agent.vo.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-08-30 14:14
 */
@Data
public class AiDeclareAccountVo {

    @ApiModelProperty("业务类型 1社保 2公积金")
    private Integer businessType;

    @ApiModelProperty("地区id")
    private Integer addressId;

    @ApiModelProperty("地区名")
    private String addressName;

    @ApiModelProperty("申报类型")
    private Integer declareType;
}
