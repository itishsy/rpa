package com.seebon.rpa.entity.agent.dto.declare;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("参保情况汇总信息")
@Data
public class DeclareSummaryDTO implements Serializable {

    @ExcelProperty(value = "序号",index = 0)
    @ColumnWidth(10)
    private Integer index;

    @ExcelIgnore
    @ApiModelProperty("客户id")
    private Integer custId;

    @ExcelProperty(value = "参保城市",index = 1)
    @ColumnWidth(20)
    @ApiModelProperty("参保城市")
    private String addrName;

    @ExcelIgnore
    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ExcelProperty(value = "业务类型",index = 2)
    @ColumnWidth(15)
    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ExcelProperty(value = "申报单位",index = 3)
    @ColumnWidth(25)
    @ApiModelProperty("申报单位")
    private String orgName;

    @ExcelProperty(value = "申报账户",index = 4)
    @ColumnWidth(20)
    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ExcelIgnore
    @ApiModelProperty("总数")
    private Integer totalCount;

    @ExcelProperty(value = "申报成功",index = 5)
    @ColumnWidth(20)
    @ApiModelProperty("申报成功")
    private Integer successCount;

    @ExcelProperty(value = "申报失败",index = 6)
    @ColumnWidth(20)
    @ApiModelProperty("申报失败")
    private Integer failCount;

    @ExcelProperty(value = "申报中",index = 7)
    @ColumnWidth(20)
    @ApiModelProperty("申报中")
    private Integer inProgressCount;

    @ExcelProperty(value = "申报中-网站待审核",index = 8)
    @ColumnWidth(20)
    @ApiModelProperty("申报中-网站待审核")
    private Integer auditCount;

    @ExcelProperty(value = "待申报",index = 9)
    @ColumnWidth(20)
    @ApiModelProperty("待申报")
    private Integer beDeclaredCount;

    @ExcelIgnore
    @ApiModelProperty("申报单位id")
    private Integer accountBaseId;

}
