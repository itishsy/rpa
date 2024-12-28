package com.seebon.rpa.entity.saas.dto.payFee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustPayFeeRequestDTO implements Serializable {

    @ApiModelProperty(value = "参保城市", required = true)
    private String addrName;

    @ApiModelProperty(value = "业务类型，1：社保，2：公积金", required = true)
    private Integer businessType;

    @ApiModelProperty(value = "社保申报系统对应字典值10007，或公积金类型对应字典值10004", required = true)
    private String systemType;

    @ApiModelProperty(value = "申报账户", required = true)
    private String accountNumber;

    @ApiModelProperty(value = "费款所属期", required = true)
    private String periodOfPayment;

    @ApiModelProperty(value = "缴费类型对应字典值10019", required = true)
    private String feeType;

    @ApiModelProperty(value = "缴费方式 1：手动缴费，2：自动缴费（仅核定）,不传默认为1", required = false)
    private Integer billType;

    @ApiModelProperty(value = "公积金比例", required = false)
    private Double ratio;

    @ApiModelProperty(value = "缴费信息状态 0：未核定，1：已核定未缴费，2：已缴费，3：缴费异常", required = true)
    private Integer status;
}
