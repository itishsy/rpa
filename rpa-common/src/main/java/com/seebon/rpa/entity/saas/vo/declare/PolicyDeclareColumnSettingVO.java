package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 15:32:31
 */
@ApiModel("报盘字段信息vo")
@Data
public class PolicyDeclareColumnSettingVO extends PolicyDeclareColumnSetting {

    @ApiModelProperty("映射值")
    private List<PolicyDeclareColumnConstantSetting> columnConstantSettings;

    @ApiModelProperty("字段取值范围")
    private List<PolicyDeclareColumnScope> columnScopes;

    @ApiModelProperty("字段源信息")
    private PolicyDeclareDataSourceItem sourceItem;

    @ApiModelProperty("是否开放")
    private Boolean openFiled = true;

    @ApiModelProperty("是否为条件字段，true:是，false:否")
    private Boolean conditionField;

    private String oldColumnName;

    @ApiModelProperty("字段校验规则信息")
    private List<PolicyDeclareColumnRuleVO> rules;

}
