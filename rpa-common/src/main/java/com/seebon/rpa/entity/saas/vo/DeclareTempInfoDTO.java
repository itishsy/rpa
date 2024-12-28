package com.seebon.rpa.entity.saas.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("报盘模板字段信息")
@Data
public class DeclareTempInfoDTO implements Serializable {

    @ApiModelProperty("申报数据所在sheet的索引")
    private Integer sheetIndex;

    @ApiModelProperty("表头最大行的行索引")
    private Integer heardIndex;

    @ApiModelProperty("文件后缀")
    private String fileSuffix;

    @ApiModelProperty("字段信息")
    private List<DeclareTempColInfoDTO> cols;

}
