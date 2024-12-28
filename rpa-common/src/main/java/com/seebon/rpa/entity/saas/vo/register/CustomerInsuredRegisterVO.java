package com.seebon.rpa.entity.saas.vo.register;

import com.seebon.rpa.entity.saas.po.register.CustomerInsuredRegister;
import lombok.Data;

@Data
public class CustomerInsuredRegisterVO extends CustomerInsuredRegister {

    private Integer orgId;

    private String orgName;

}
