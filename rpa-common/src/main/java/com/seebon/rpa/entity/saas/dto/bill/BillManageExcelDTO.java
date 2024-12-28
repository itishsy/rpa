package com.seebon.rpa.entity.saas.dto.bill;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-17 13:39
 */
@Data
@ApiModel("账单excel")
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
@ContentStyle(wrapped=true)
public class BillManageExcelDTO implements Serializable {

    @ApiModelProperty("收款单号")
    @ExcelProperty(index=0, value="收款单号")
    private String receiptNumber;

    @ApiModelProperty("客户名称")
    @ExcelProperty(index=1, value="客户名称")
    private String customerName;

    @ApiModelProperty("收款年月")
    @ExcelProperty(index=2, value="收款年月")
    private String receiptYearMonth;

    @ApiModelProperty("费用项目 0在户人数  1驻场服务 2外勤服务 3其他 ，多个用逗号隔开")
    @ExcelProperty(index=3, value="费用项目")
    private String receiptProject;

    @ApiModelProperty("收款单金额")
    @ExcelProperty(index=4, value="收款单金额")
    @ContentStyle(dataFormat = 2)
    private BigDecimal payCount;

    @ApiModelProperty("约定收款日")
    @ExcelProperty(index=5, value="约定收款日")
    private String agreeReceiptDay;

    @ApiModelProperty("到账时间")
    @ExcelProperty(index=6, value="到账时间")
    private String payTime;

    @ApiModelProperty("实收金额")
    @ExcelProperty(index=7, value="实收金额")
    @ContentStyle(dataFormat = 2)
    private BigDecimal actualAmount;

    @ApiModelProperty("在户服务")
    @ExcelProperty(index=8, value="在户服务")
    @ContentStyle(dataFormat = 2)
    private BigDecimal zhAmount;

    @ApiModelProperty("驻场服务")
    @ExcelProperty(index=9, value="驻场服务")
    @ContentStyle(dataFormat = 2)
    private BigDecimal zcAmount;

    @ApiModelProperty("外勤服务")
    @ExcelProperty(index=10, value="外勤服务")
    @ContentStyle(dataFormat = 2)
    private BigDecimal wqAmount;

    @ApiModelProperty("其他服务")
    @ExcelProperty(index=11, value="其他服务")
    @ContentStyle(dataFormat = 2)
    private BigDecimal qtAmount;

    @ApiModelProperty("审核人")
    @ExcelProperty(index=12, value="审核人")
    private String checkName;

    @ApiModelProperty("审核时间")
    @ExcelProperty(index=13, value="审核时间")
    private String checkTime;

    @ApiModelProperty("创建人")
    @ExcelProperty(index=14, value="创建人")
    private String createName;

    @ApiModelProperty("创建时间")
    @ExcelProperty(index=15, value="创建时间")
    private String createTime;

    @ApiModelProperty("处理状态")
    @ExcelProperty(index=16, value="处理状态")
    private String handleStatusStr;

    @ApiModelProperty("开票状态")
    @ExcelProperty(index=17, value="开票状态")
    private String invoiceStatusStr;

}
