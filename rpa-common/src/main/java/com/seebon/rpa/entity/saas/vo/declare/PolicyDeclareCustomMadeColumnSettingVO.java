package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnScope;
import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareCustomMadeColumnSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("定制报盘字段信息VO")
@Data
public class PolicyDeclareCustomMadeColumnSettingVO extends PolicyDeclareCustomMadeColumnSetting {

    @ApiModelProperty("字段取值范围")
    private List<PolicyDeclareColumnScope> columnScopes;

    @ApiModelProperty("是否定制字段，1：是，0：否")
    private Integer customMadeField;

    private Integer input;

    private Integer output;

}
