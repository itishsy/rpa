package com.seebon.rpa.entity.saas.po.bill;

import com.seebon.rpa.entity.saas.dto.bill.UploadFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-04 18:07
 */
@ApiModel("收款明细")
@Data
public class ReceiptDetail{
    @ApiModelProperty("费用年月")
    private String payYearMonth;

    @ApiModelProperty("服务类型  0在户人数  1驻场服务 2外勤服务 3其他服务")
    private Integer serviceType;

    @ApiModelProperty("费用项目")
    private String payProject;

    @ApiModelProperty("数量")
    private Integer personNumber;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("总额")
    private BigDecimal countPrice;

    @ApiModelProperty("文件集合")
    private List<UploadFileDTO> payFiledList;
}
