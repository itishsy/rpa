package com.seebon.rpa.entity.saas.dto.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnConstantSetting;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hao
 * @Date 2022/5/10 17:59
 * @Version 1.0
 **/
@Data
public class ExcelTemplateDTO implements Serializable {

    private String addrId;

    private String addrName;

    private String name;

    private Integer bussinssType;

    private Integer type;

    private Integer changeType;

    private String dataSourceItemCode;

    private List<PolicyDeclareColumnConstantSetting> constantSettings;

}
