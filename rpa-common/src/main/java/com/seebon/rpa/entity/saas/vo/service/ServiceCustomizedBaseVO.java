package com.seebon.rpa.entity.saas.vo.service;

import com.seebon.rpa.entity.saas.po.service.ServiceCustomizedBase;
import com.seebon.rpa.entity.saas.vo.declare.account.DeclareAccountItemVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ServiceCustomizedBaseVO extends ServiceCustomizedBase {

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

}
