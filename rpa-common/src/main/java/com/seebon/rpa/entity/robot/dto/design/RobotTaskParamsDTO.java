package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("申报配置-批量启停申报账户查询参数")
@Data
public class RobotTaskParamsDTO implements Serializable {

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("业务类型，1001001：社保，1001002：公积金")
    private List<String> businessTypes;

    @ApiModelProperty("是否停用")
    private Boolean isStop;

}
