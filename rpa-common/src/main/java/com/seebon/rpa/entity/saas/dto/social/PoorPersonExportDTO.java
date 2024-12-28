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

import java.math.BigDecimal;

/**
 * 符合贫困人口表
 */
@Data
@ApiModel("贫困人员")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped = true)
public class PoorPersonExportDTO {
    @ApiModelProperty("员工姓名")
    @ExcelProperty(index = 0, value = "员工姓名")
    @ColumnWidth(20)
    private String name;
    @ApiModelProperty("身份证号")
    @ExcelProperty(index = 1, value = "身份证号")
    @ColumnWidth(20)
    private String idCard;

    @ApiModelProperty("入职日期")
    @ExcelProperty(index = 2, value = "入职日期")
    @ColumnWidth(20)
    private String entryTime;
    @ApiModelProperty("失业符合月份")
    @ExcelProperty(index = 3, value = "失业符合月份")
    @ColumnWidth(20)
    private BigDecimal amount;
    @ApiModelProperty("失业金额")
    @ExcelProperty(index = 4, value = "失业金额")
    @ColumnWidth(20)
    private Integer monthCount;

}