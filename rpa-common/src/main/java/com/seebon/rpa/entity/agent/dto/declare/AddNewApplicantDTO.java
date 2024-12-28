package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddNewApplicantDTO {
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String name;

    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "证件类型")
    private String cardType;
}
