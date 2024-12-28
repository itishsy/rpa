package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-10 18:33:30
 */
@ApiModel("参保录入信息")
@Data
public class EmployeeDeclareSaveDTO {

    @ApiModelProperty("保存类型，0：保存，1：保存并提交")
    private Integer saveType;

    @ApiModelProperty("参保地名称")
    private String addrName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("是否校验减员其他申报账户 0校验  1校验")
    private Integer igCheckOtherAccount;

    @ApiModelProperty("员工申报信息")
    private Map<String, String> declareInfo;

}
