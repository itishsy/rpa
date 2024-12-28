package com.seebon.rpa.entity.saas.dto.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("导入在册参数dto")
@Data
public class CustomerInsuredImportRegisterDTO implements Serializable {

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("申报系统")
    private String tplTypeCode;

    @ApiModelProperty("参保时间，yyyy-MM")
    private String dataMonth;

    @ApiModelProperty("单位申报号或单位公积金号")
    private String accountNumber;

    @ApiModelProperty("文件id")
    private Integer fileId;

    @ApiModelProperty("名册所在sheet")
    private Integer sheetIndex;

    @ApiModelProperty("表头行号")
    private Integer headerNumber;

    @ApiModelProperty("参保城市id")
    private String idCardCol;

    @ApiModelProperty("参保城市id")
    private String empNameCol;

    @ApiModelProperty("参保城市id")
    private String empAccountCol;

    @ApiModelProperty("参保城市id")
    private String baseCol;

}
