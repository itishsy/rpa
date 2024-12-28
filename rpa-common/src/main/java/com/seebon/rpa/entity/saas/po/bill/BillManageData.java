package com.seebon.rpa.entity.saas.po.bill;

import com.seebon.rpa.entity.saas.dto.bill.UploadFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe 账单实体类
 * @date 2024-01-04 15:01
 */

@ApiModel(value ="bill_manage_data")
@Document(collection="bill_manage_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillManageData{
    @ApiModelProperty("id")
    private String _id;

    @ApiModelProperty("收款单号")
    private String receiptNumber;

    @ApiModelProperty("客户id")
    private Integer customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("收款年月")
    private String receiptYearMonth;

    @ApiModelProperty("费用项目 0在户人数  1驻场服务 2外勤服务 3其他服务 ，多个用逗号隔开")
    private String receiptProject;

    @ApiModelProperty("约定收款日")
    private String agreeReceiptDay;

    @ApiModelProperty("入账公司")
    private String receiptCompany;

    @ApiModelProperty("入账银行")
    private String receiptBlank;

    @ApiModelProperty("入账账号")
    private String receiptAccount;

    @ApiModelProperty("实收金额")
    private BigDecimal actualAmount;

    @ApiModelProperty("处理状态 0已到账  1未到账 2待审核 3审核不通过  ")
    private Integer handleStatus;

    @ApiModelProperty("开票状态 0未开票 1已开票 2部分开票")
    private Integer invoiceStatus;

    @ApiModelProperty("开票金额")
    private BigDecimal kpAmount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("创建人")
    private String createName;

    @ApiModelProperty("审核人")
    private String checkName;

    @ApiModelProperty("审核时间")
    private String checkTime;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty("驳回备注")
    private String rejectRemark;

    @ApiModelProperty("收款备注")
    private String payRemark;

    @ApiModelProperty("发票文件集合")
    private List<UploadFileDTO> fpFileList;

    @ApiModelProperty("发票备注")
    private String fpRemark;

    @ApiModelProperty("到款文件集合")
    private List<UploadFileDTO> dkFileList;

    @ApiModelProperty("费用明细集合")
    private List<ReceiptDetail> receiptDetailList;

    @ApiModelProperty("项目明细")
    private List<ReceiptProjectDetail> receiptProjectDetailList;

    @ApiModelProperty("项目合计")
    private BigDecimal projectCount;

    @ApiModelProperty("收款单金额")
    private BigDecimal payCount;

    @ApiModelProperty("到账时间")
    private String payTime;

}
