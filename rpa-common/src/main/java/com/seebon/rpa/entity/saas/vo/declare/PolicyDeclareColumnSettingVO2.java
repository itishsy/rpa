package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnSetting;
import lombok.Data;

@Data
public class PolicyDeclareColumnSettingVO2 extends PolicyDeclareColumnSetting {

    private String sourceItemCode;

    private String sourceTableCode;

    private String sourceItemName;

    private String sourceTableName;

    private String declareColumnName;
}
