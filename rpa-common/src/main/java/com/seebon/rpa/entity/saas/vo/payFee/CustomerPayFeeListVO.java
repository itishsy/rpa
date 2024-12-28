package com.seebon.rpa.entity.saas.vo.payFee;

import com.seebon.rpa.entity.saas.po.payFee.CustomerPayFeeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("客户缴费信息VO")
@Data
public class CustomerPayFeeListVO extends CustomerPayFeeList {

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

    @ApiModelProperty(value = "单据凭证", required = true)
    private List<CustomerPayFeeFileVO> files;

    @ApiModelProperty("过程")
    private List<CustomerPayFeeProcessVO> processes;

}
