package com.seebon.rpa.entity.saas.dto.declare;

import lombok.Data;

import java.util.List;

@Data
public class PolicyDeclareBaseSettingDTO {

    private String addressName;

    private Integer businessType;

    private Integer changeType;

    private List<String> tableCodes;
}
