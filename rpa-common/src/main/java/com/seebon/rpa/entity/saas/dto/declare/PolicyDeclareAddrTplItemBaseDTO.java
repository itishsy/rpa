package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-27 14:40:38
 */
@ApiModel("模板险种设置信息-base")
@Data
public class PolicyDeclareAddrTplItemBaseDTO {

    @ApiModelProperty("参保地id")
    private Integer addrId;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型 1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("模板险种信息")
    private List<PolicyDeclareAddrTplItemDTO> tplItems;

}
