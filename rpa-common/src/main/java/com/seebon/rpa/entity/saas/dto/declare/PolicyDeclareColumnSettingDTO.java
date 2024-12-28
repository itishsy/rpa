package com.seebon.rpa.entity.saas.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author mao
 * @Date 2022/4/24 16:07
 * @Version 1.0
 **/
@Data
@ApiModel("生成报盘文件DTO")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
public class PolicyDeclareColumnSettingDTO {

    @ApiModelProperty("字段名")
    @ExcelProperty(index = 0, value = "字段名")
    @ColumnWidth(20)
    private String declareColumnName;

    @ApiModelProperty("字段类型（1：文本，2：下拉，3：数值，4：年月日，5：年月）")
    @ExcelProperty(index = 1, value = "字段类型")
    @ColumnWidth(20)
    private Integer inputType;

    @ApiModelProperty("数据源表，关联policy_declare_data_source_table code值")
    @ExcelProperty(index = 2, value = "数据源")
    @ColumnWidth(20)
    private String dataSourceTableCode;

    @ApiModelProperty("数据源字段，关联policy_declare_data_source_item code值")
    @ExcelProperty(index = 3, value = "数据源字段")
    @ColumnWidth(20)
    private String dataSourceItemCode;

    @ApiModelProperty("默认值")
    @ExcelProperty(index = 4, value = "默认值")
    @ColumnWidth(20)
    private String defaultValue;

    @ApiModelProperty("导出格式")
    @ExcelProperty(index = 5, value = "导出格式")
    @ColumnWidth(20)
    private String exportFormat;

    @ApiModelProperty("展示顺序")
    @ExcelProperty(index = 6, value = "展示顺序")
    @ColumnWidth(20)
    private Integer sort;

    @ApiModelProperty("备注")
    @ExcelProperty(index = 7, value = "备注")
    @ColumnWidth(20)
    private String comment;

    @ApiModelProperty("系统获取值")
    @ExcelProperty(index = 8, value = "系统获取值")
    @ColumnWidth(20)
    private String originalText;

    @ApiModelProperty("需要显示到报盘模板的文本")
    @ExcelProperty(index = 9, value = "需要显示到报盘模板的文本")
    @ColumnWidth(20)
    private String showText;

    @ApiModelProperty("是否显示 1：是，0：否")
    @ExcelProperty(index = 10, value = "是否显示")
    @ColumnWidth(20)
    private String isShow;

}
