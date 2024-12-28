package com.seebon.rpa.entity.saas.dto.register;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RegisterParamsDTO implements Serializable {

    private Integer custId;

    private String month;

    private List<String> addrList;

    private List<String> notAddrList;

}
