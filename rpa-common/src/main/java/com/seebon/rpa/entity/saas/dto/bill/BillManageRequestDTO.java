package com.seebon.rpa.entity.saas.dto.bill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe 账单管理请求vo
 * @date 2024-01-12 15:41
 */
@Data
public class BillManageRequestDTO implements Serializable {
    @ApiModelProperty("id")
    private String _id;

    @ApiModelProperty("实收金额")
    private BigDecimal actualAmount;

    @ApiModelProperty("开票金额")
    private BigDecimal kpAmount;

    @ApiModelProperty("文件集合")
    private List<UploadFileDTO> fileList;

    @ApiModelProperty("备注")
    private String payRemark;

    @ApiModelProperty("开票状态 0未开票 1已开票 2部分开票")
    private Integer invoiceStatus;

    @ApiModelProperty("处理状态 0已到账  1未到账 2待审核 3审核不通过  ")
    private Integer handleStatus;

    @ApiModelProperty("驳回备注")
    private String rejectRemark;

    @ApiModelProperty("ids")
    private List<String> ids;

}
