package com.seebon.rpa.entity.robot.dto.monitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("今日执行信息DTO")
@Data
public class TodayExecDTO implements Serializable {


    @ApiModelProperty("临期")
    private Integer noDeclaredCount;

    @ApiModelProperty("列表")
    private List<TodayExecDataDTO> list;

}
