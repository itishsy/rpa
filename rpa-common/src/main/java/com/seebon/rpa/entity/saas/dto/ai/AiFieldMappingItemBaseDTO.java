package com.seebon.rpa.entity.saas.dto.ai;
import cn.afterturn.easypoi.excel.annotation.Excel;
import com.seebon.rpa.entity.saas.po.ai.AiFieldMappingDeclareItem;
import com.seebon.rpa.entity.saas.po.ai.AiFieldMappingInputItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("映射信息DTO")
@Data
public class AiFieldMappingItemBaseDTO {

    @ApiModelProperty(value = "itemUuid")
    private String uuid;

    @ApiModelProperty(value="序号", required = false, notes = "用于展示")
    @Excel(isWrap = false, name = "映射组", orderNum = "2", width = 10)
    private Integer index;

    @ApiModelProperty(value = "映射字段原值", required = true, notes = "文本域输入的原值")
    private String fieldValue;

    @ApiModelProperty(value = "映射字段原值导出", notes = "用于导出")
    @Excel(isWrap = false, name = "字段填值", orderNum = "3", width = 60)
    private String inputExportValue;

    @ApiModelProperty(value = "申报值导出", notes = "用于导出")
    @Excel(isWrap = false, name = "申报值", orderNum = "4", width = 60)
    private String declareExportValue;

    @ApiModelProperty("状态，1：启用，0：禁用")
    @Excel(isWrap = false, name = "状态", orderNum = "4", width = 10, replace={"启用_1","禁用_0"})
    private Integer status = 1;

    @ApiModelProperty(value = "字段填值", required = true)
    List<AiFieldMappingInputItem> inputItems;

    @ApiModelProperty(value = "申报值（只保存勾选的）", required = true)
    List<AiFieldMappingDeclareItem> declareItems;

}
