package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareBaseCustomMadeSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("定制报盘设置基础信息VO")
@Data
public class PolicyDeclareBaseCustomMadeSettingVO extends PolicyDeclareBaseCustomMadeSetting {

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("申报类型")
    private Integer changeType;

    @ApiModelProperty("申报类型名称")
    private String changeTypeName;

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("定制字段数")
    private Integer fieldNumber;

    @ApiModelProperty("定制字段信息")
    private List<PolicyDeclareCustomMadeColumnSettingVO> columnSettings;

}
