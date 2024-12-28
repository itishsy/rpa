package com.seebon.rpa.entity.robot.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskInfoDTO implements Serializable {

    private Integer custId;

    private Integer addrId;

    private String accountNumber;

    private String addrName;

    private String businessType;

}
