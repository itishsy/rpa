package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnConstantSetting;
import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareDataSourceItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-24 14:34:57
 */
@ApiModel("报盘设置数据源字段信息VO")
@Data
public class PolicyDeclareDataSourceItemVO extends PolicyDeclareDataSourceItem {

    @ApiModelProperty("字段取值选项")
    private List<PolicyDeclareColumnConstantSetting> constantSettingList;

}
