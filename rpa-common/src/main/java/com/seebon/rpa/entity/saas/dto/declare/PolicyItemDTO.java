package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-27 14:58:43
 */
@ApiModel("险种信息")
@Data
public class PolicyItemDTO {

    @ApiModelProperty("险种编码")
    private String itemCode;

    @ApiModelProperty("险种名称")
    private String itemName;

}
