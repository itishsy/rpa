package com.seebon.rpa.entity.agent.vo.declare.employee;


import com.seebon.rpa.entity.agent.dto.declare.InsuranceTypeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SocialAddrVO {
    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("参保险种名称")
    private String itemCode;

    @ApiModelProperty("单位社保号")
    private String compAccount;

    @ApiModelProperty("单位社保号ID")
    private Integer compAccountId;

    @ApiModelProperty("组织名称")
    private String orgName;

    @ApiModelProperty("险种集合")
    private List<InsuranceTypeDTO> itemCodes;
}
