package com.seebon.rpa.entity.agent.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeclareValidateQueryDTO {

    private Integer custId;

    private String accountNumber;

    private String addrName;

    private String createMonth;

    private Integer businessType;

    private String beginDate;

    private String endDate;

    private List<Integer> validateStatus;

    private String[] distinctBy;

}
