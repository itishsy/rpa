package com.seebon.rpa.entity.saas.vo.service;

import com.seebon.rpa.entity.saas.po.service.ServiceCustomizedConsumption;
import com.seebon.rpa.entity.saas.vo.declare.account.DeclareAccountItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("定制服务消费信息VO")
@Data
public class ServiceCustomizedConsumptionVO extends ServiceCustomizedConsumption {

    @ApiModelProperty("'id'")
    private Integer sid;

    @ApiModelProperty("'一级定制代码'")
    private String firstCode;

    @ApiModelProperty("定制代码")
    private String secondCode;

    @ApiModelProperty("适配报盘字段")
    private String adaptFieldName;

    @ApiModelProperty("定制内容")
    private String content;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("申报账户")
    private String declareAccount;

    @ApiModelProperty("服务板块名称")
    private String serviceBlockName;

    @ApiModelProperty("业务类型名称")
    private String businessTypeName;

    @ApiModelProperty("数据范围名称")
    private String dataRangeName;

    private List<DeclareAccountItemVO> declareAccountItems;

    @ApiModelProperty("提示信息")
    private String promptInfo;

    @ApiModelProperty("备注")
    private String comments;

    @ApiModelProperty("异常信息反馈")
    private String errorMessage;

    @ApiModelProperty("服务板块")
    private String serviceBlockCode;

    @ApiModelProperty("适用范围 1：该户全部数据，2：标定的数据")
    private Integer dataRange;

}
