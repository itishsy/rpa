package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QueryByAllDTO {

    @ApiModelProperty("身份证")
    private List<String> idCard;

    @ApiModelProperty("姓名")
    private List<String> name;

    @ApiModelProperty("查询状态：1未投保，2投保办理中，3调整办理中，4停保办理中，5正常在缴，6已停保")
    private Integer status;
}
