package com.seebon.rpa.entity.agent.dto.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author LY
 * @date 2023/7/5 16:04
 */
@Data
public class InsuredChangeBatchDTO {

    @ApiModelProperty("'业务类型 1：社保，2：公积金'")
    private Integer businessType;

    private List<InsuredChangeItemDTO> items;

    @ApiModelProperty("备注")
    private String remark;


}
