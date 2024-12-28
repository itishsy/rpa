package com.seebon.rpa.entity.robot.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("数据处理")
@Data
public class DatabaseHandleParamsDTO {

    @ApiModelProperty("执行类型")
    private String exeType;

    @ApiModelProperty("执行语句")
    private String sql;

    @ApiModelProperty("执行结果")
    private String dataKey;
}
