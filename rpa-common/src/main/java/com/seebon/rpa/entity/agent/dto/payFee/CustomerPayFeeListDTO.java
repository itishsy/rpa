package com.seebon.rpa.entity.agent.dto.payFee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class CustomerPayFeeListDTO implements Serializable {

    private Integer id;

    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("客户id，对应表customer_base id值")
    private Integer custId;

    @ApiModelProperty(value = "参保城市id", required = true)
    private Integer addrId;

    @ApiModelProperty(value = "参保城市", required = true)
    private String addrName;

    @ApiModelProperty(value = "业务类型，1：社保，2：公积金", required = true)
    private Integer businessType;

    @ApiModelProperty(value = "社保申报系统对应字典值10007，或公积金类型对应字典值10004", required = true)
    private String systemType;

    @ApiModelProperty(value = "申报账户", required = true)
    private String accountNumber;

    @ApiModelProperty(value = "缴费类型对应字典值10019", required = true)
    private String feeType;

    @ApiModelProperty("公积金比例")
    private Double ratio;

    @ApiModelProperty(value = "缴费方式 1：手动缴费，2：自动缴费", required = true)
    private Integer billType;

    @ApiModelProperty(value = "费款所属期", required = true)
    private String periodOfPayment;

    @ApiModelProperty(value = "缴费人数", required = true)
    private Integer payNumber;

    @ApiModelProperty(value = "缴费金额", required = true)
    private Double amount;

    @ApiModelProperty("缴费状态0：未核对，1：已核定未交费，2：已缴费，3：缴费异常")
    private Integer status;

    @ApiModelProperty("流水号")
    private String serialNumber;

    @ApiModelProperty("备注")
    private String comment;

    private Integer createId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Integer updateId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("申报单位名称")
    private Integer orgId;

    @ApiModelProperty("申报单位名称")
    private String orgName;

    @ApiModelProperty("申报账户")
    private String accountNo;

    @ApiModelProperty("社保申报系统or公积金类型")
    private String systemTypeName;

    @ApiModelProperty("缴费类型名称")
    private String feeTypeName;

    @ApiModelProperty("缴费状态名称")
    private String statusName;

    @ApiModelProperty("缴费方式名称")
    private String billTypeName;

    @ApiModelProperty("缴费制单计划")
    private String billPlant;

}
