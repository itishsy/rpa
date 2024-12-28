package com.seebon.rpa.entity.saas.dto.declare;

import com.seebon.rpa.entity.saas.vo.declare.PolicyProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("新增参保险种和关联标记信息")
@Data
public class PolicyDeclareProductTplDTO implements Serializable {

    @ApiModelProperty("关联标记信息")
    private PolicyDeclareAddrTplItemBaseDTO tplItemDTO;

    @ApiModelProperty("参保险种信息")
    private List<PolicyProductVO> products;

    @ApiModelProperty("参保险种信息-单个编辑")
    private PolicyProductVO product;
}
