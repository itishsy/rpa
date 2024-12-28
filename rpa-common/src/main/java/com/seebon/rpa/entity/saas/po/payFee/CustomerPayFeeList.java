package com.seebon.rpa.entity.saas.po.payFee;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("客户缴费信息PO")
@Table(name = "customer_pay_fee_list")
@Data
public class CustomerPayFeeList extends Identity {

    @ApiModelProperty("uuid")
    @Column
    private String uuid;

    @ApiModelProperty("客户id，对应表customer_base id值")
    @Column
    private Integer custId;

    @ApiModelProperty(value = "参保城市id", required = true)
    @Column
    private Integer addrId;

    @ApiModelProperty(value = "参保城市", required = true)
    @Column
    private String addrName;

    @ApiModelProperty(value = "业务类型，1：社保，2：公积金", required = true)
    @Column
    private Integer businessType;

    @ApiModelProperty(value = "社保申报系统对应字典值10007，或公积金类型对应字典值10004", required = true)
    @Column
    private String systemType;

    @ApiModelProperty(value = "申报账户", required = true)
    @Column
    private String accountNumber;

    @ApiModelProperty(value = "缴费类型对应字典值10019", required = true)
    @Column
    private String feeType;

    @ApiModelProperty("公积金比例")
    @Column
    private Double ratio;

    @ApiModelProperty(value = "缴费方式 1：手动缴费，2：自动缴费", required = true)
    @Column
    private Integer billType;

    @ApiModelProperty(value = "费款所属期", required = true)
    @Column
    private String periodOfPayment;

    @ApiModelProperty(value = "缴费人数", required = true)
    @Column
    private Integer payNumber;

    @ApiModelProperty(value = "缴费金额", required = true)
    @Column
    private Double amount;

    @ApiModelProperty("缴费状态0：未核对，1：已核定未交费，2：已缴费，3：缴费异常")
    @Column
    private Integer status;

    @ApiModelProperty("流水号")
    @Column
    private String serialNumber;

    @ApiModelProperty("备注")
    @Column
    private String comment;

}
