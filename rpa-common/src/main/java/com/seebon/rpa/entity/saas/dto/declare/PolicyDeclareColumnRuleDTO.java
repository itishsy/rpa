package com.seebon.rpa.entity.saas.dto.declare;

import com.seebon.rpa.entity.saas.vo.declare.PolicyDeclareColumnRuleVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("")
@Data
public class PolicyDeclareColumnRuleDTO {

    @ApiModelProperty("报盘字段uuid")
    private String uuid;

    @ApiModelProperty("报盘字段名称")
    private String declareColumnName;

    @ApiModelProperty("校验是否涉及入数据，1：是，0：否")
    private Integer inFlag;

    @ApiModelProperty("校验是否涉及导报盘，1：是，0：否")
    private Integer outFlag;

    @ApiModelProperty("字段规则信息")
    List<PolicyDeclareColumnRuleVO> rules;


}
