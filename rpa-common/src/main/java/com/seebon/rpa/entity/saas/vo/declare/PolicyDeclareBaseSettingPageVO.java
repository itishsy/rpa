package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareBaseSetting;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author mao
 */
@Data
public class PolicyDeclareBaseSettingPageVO extends PolicyDeclareBaseSetting {

    @ApiModelProperty("关联文件信息")
    private List<PolicyDeclareBaseTplVO> tpls;

    @ApiModelProperty("定制报盘数")
    private Integer customMadeNumber;

    @ApiModelProperty("校验规则数")
    private Integer ruleNumber;
}
