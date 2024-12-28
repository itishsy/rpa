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
@ApiModel("社保费用表")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped = true)
public class SocialCostDTO {
    @ApiModelProperty("姓名")
    @ExcelProperty(index = 0, value = "姓名")
    @ColumnWidth(20)
    private String name;

    @ApiModelProperty("身份证号")
    @ExcelProperty(index = 1, value = "身份证号")
    @ColumnWidth(20)
    private String idCard;

    @ApiModelProperty("验证结果")
    @ExcelProperty(index = 2, value = "验证结果")
    @ColumnWidth(20)
    private String checkResult;

    @ApiModelProperty("文件名称")
    @ExcelProperty(index = 3, value = "文件名称")
    @ColumnWidth(20)
    private String fileName;

    @ApiModelProperty("sheet名称")
    private String sheetName;

    @ApiModelProperty("入职时间")
    private String entryTime;
}
