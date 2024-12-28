package com.seebon.rpa.entity.agent.dto.declare.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("调整申报失败数据忽略标记传参")
@Data
public class EmployeeDeclareIgnoreParamsDTO implements Serializable {

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("忽略的社保险种code，只有忽略需要")
    private List<String> itemCodes;

    @ApiModelProperty("忽略标记，1：忽略，0：恢复")
    private Integer ignoreFlag;

    @ApiModelProperty("参保记录的uuid")
    private List<String> changeUuids;

}
