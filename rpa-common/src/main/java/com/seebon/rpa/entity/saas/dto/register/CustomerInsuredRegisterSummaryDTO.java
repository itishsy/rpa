package com.seebon.rpa.entity.saas.dto.register;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("在户明细汇总信息")
@Data
public class CustomerInsuredRegisterSummaryDTO implements Serializable {

    @ExcelProperty(value = "客户名称", index=0)
    @ApiModelProperty("客户名称")
    private String custName;

    @ExcelProperty(value = "申报单位", index=1)
    @ApiModelProperty("申报单位")
    private String orgName;

    @ExcelProperty(value = "姓名", index=2)
    @ApiModelProperty("姓名")
    private String empName;

    @ExcelProperty(value = "证件号码", index=3)
    @ApiModelProperty("证件号码")
    private String idCard;

    @ExcelProperty(value = "参保城市", index=4)
    @ApiModelProperty("参保城市")
    private String addrName;

    @ExcelProperty(value = "参保年月", index=5)
    @ApiModelProperty("参保年月")
    private String dataMonth;

    @ExcelProperty(value = "个人社保号", index=6)
    @ApiModelProperty("个人社保号")
    private String empSocialAccount;

    @ExcelProperty(value = "社保基数", index=7)
    @ApiModelProperty("社保基数")
    private String socialBase;

    @ExcelProperty(value = "个人公积金号", index=8)
    @ApiModelProperty("个人公积金号")
    private String empAccfundAccount;

    @ExcelProperty(value = "公积金基数", index=9)
    @ApiModelProperty("公积金基数")
    private String accfundBase;

}
