package com.seebon.rpa.entity.saas.vo.payFee;

import com.seebon.rpa.entity.saas.po.system.SysFilestore;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("SysFilestoreVO")
@Data
public class SysFilestoreVO extends SysFilestore {

    private Integer payFeeId;

    private String payUuid;

    private String orgName;

    private String accountNumber;

    private String addrName;

    private String systemTypeName;

    private String periodOfPayment;

}
