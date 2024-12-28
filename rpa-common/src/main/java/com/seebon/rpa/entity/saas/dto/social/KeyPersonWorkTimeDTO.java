package com.seebon.rpa.entity.saas.dto.social;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel("重点人员工作时间")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped = true)
public class KeyPersonWorkTimeDTO {

    @ApiModelProperty("招用人姓名")
    @ExcelProperty(index = 0, value = "招用人姓名")
    @ColumnWidth(20)
    private String name;


    @ApiModelProperty("身份证件号码")
    @ExcelProperty(index = 1, value = "身份证件号码")
    @ColumnWidth(20)
    private String idCard;

    @ApiModelProperty("类型01失业  04贫困")
    @ExcelProperty(index = 2, value = "类型01失业  04贫困")
    private String type ;

    @ApiModelProperty("入职时间")
    @ExcelProperty(index = 3, value = "入职时间")
    private String entryTime;
}
