package com.seebon.rpa.entity.saas.vo.declare;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InsuranceTypeVO {

    @ApiModelProperty("险种名称")
    private String itemName;
    @ApiModelProperty("险种编码")
    private String itemCode;
    @ApiModelProperty("默认不参保 1:是，0:否")
    private Integer defaultInsured;
}
