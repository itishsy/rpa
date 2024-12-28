package com.seebon.rpa.entity.saas.dto.social;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel("导出采集表")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped = true)
public class ExportCollectionDTO {
    @ApiModelProperty("序号")
    @ExcelProperty(index = 0, value = "序号")
    @ColumnWidth(20)
    private Integer index;

    @ApiModelProperty("招用人姓名")
    @ExcelProperty(index = 1, value = "招用人姓名")
    @ColumnWidth(20)
    private String name;

    @ApiModelProperty("身份证件类型")
    @ExcelProperty(index = 2, value = "身份证件类型")
    @ColumnWidth(20)
    private String idCardType;

    @ApiModelProperty("身份证件号码")
    @ExcelProperty(index = 3, value = "身份证件号码")
    @ColumnWidth(20)
    private String idCard;

    @ApiModelProperty("证件编号")
    @ExcelProperty(index = 4, value = "证件编号")
    private String cardNumber;

    @ApiModelProperty("类型(1)(2)(3)(4)")
    @ExcelProperty(index = 4, value = "类型(1)(2)(3)(4)")
    private String type;

    @ApiModelProperty("在本企业工作时间（月）")
    @ExcelProperty(index = 4, value = "在本企业工作时间（月）")
    private Integer monthCount;

}
