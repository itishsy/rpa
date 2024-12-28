package com.seebon.rpa.entity.saas.dto.ai;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("api值映射保存信息DTO")
@Data
public class AiFieldMappingBaseSaveDTO implements Serializable {

    @ApiModelProperty(value = "主键id，编辑时必填", notes = "编辑时必填")
    private Integer id;

    @ApiModelProperty(value = "uuid，编辑时必填", notes = "编辑时必填")
    private String uuid;

    @ApiModelProperty(value = "字段名集合", notes = "保存时无需返回信息")
    @Excel(isWrap = false, name = "字段名称", orderNum = "1", width = 40, needMerge = true)
    private String name;

    @ApiModelProperty(value = "字段名集合", notes = "保存时无需返回信息")
    private String fieldJson;

    @ApiModelProperty(value = "字段名集合", required = true)
    private List<String> fields;

    @ApiModelProperty(value = "映射信息", required = true)
    @ExcelCollection(name="", orderNum="2")
    private List<AiFieldMappingItemBaseDTO> itemBases;

}
