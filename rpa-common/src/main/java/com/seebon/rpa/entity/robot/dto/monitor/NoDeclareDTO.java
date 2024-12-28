package com.seebon.rpa.entity.robot.dto.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("未申报数据")
@Data
public class NoDeclareDTO implements Serializable {

    @ApiModelProperty("申报类型code")
    private Integer changeType;

    @ApiModelProperty("申报类型")
    private String changeTypeName;

    @ApiModelProperty("未申报")
    private Integer empCount;

    @ApiModelProperty("未报时长-分钟")
    private Integer minuteTime;

    @ApiModelProperty("当月成功数")
    private Integer successCount;

    @ApiModelProperty("当月失败数")
    private Integer failCount;

    @ApiModelProperty("是否异常，1：是，0：否")
    private Integer error;

    @ApiModelProperty("流程编号")
    private List<String> flowCodes;

}
