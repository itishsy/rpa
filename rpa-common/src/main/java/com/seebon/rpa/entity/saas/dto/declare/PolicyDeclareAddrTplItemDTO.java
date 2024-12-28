package com.seebon.rpa.entity.saas.dto.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyAddrMergeAddRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-27 14:42:59
 */
@ApiModel("模板险种信息")
@Data
public class PolicyDeclareAddrTplItemDTO {

    @ApiModelProperty("模板类型")
    private String tplType;

    @ApiModelProperty("模板类型名称")
    private String tplTypeName;

    @ApiModelProperty("模板关联险种信息")
    private List<PolicyItemDTO> items;

    @ApiModelProperty("是否增补合并申报")
    private Boolean merge;

    @ApiModelProperty("增补合并规则信息")
    private PolicyAddrMergeAddRule mergeAddRule;

}
