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
public class LostWorkExportDTO {
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

    @ApiModelProperty("发证机构")
    @ExcelProperty(index = 5, value = "发证机构")
    @ColumnWidth(20)
    private String certOrg;

    @ApiModelProperty("发证时间")
    @ExcelProperty(index = 6, value = "发证时间")
    @ColumnWidth(20)
    private String certTime;

    @ApiModelProperty("发证编号")
    @ExcelProperty(index = 7, value = "发证编号")
    @ColumnWidth(20)
    private String certNo;

    @ApiModelProperty("发证地区 不一定有值")
    @ExcelProperty(index = 8, value = "发证地区")
    @ColumnWidth(20)
    private String certAddr;

    @ApiModelProperty("证书更新日期  不一定有值")
    @ExcelProperty(index = 9, value = "证书更新日期")
    @ColumnWidth(20)
    private String certUpdateTime;

}
