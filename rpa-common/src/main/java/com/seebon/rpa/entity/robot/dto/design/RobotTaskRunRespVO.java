package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("指定任务流程信息")
@Data
public class RobotTaskRunRespVO {

    @ApiModelProperty("机器标识码")
    private String machineCode;

    @ApiModelProperty("应用编码")
    private String appCode;

    @ApiModelProperty("流程编码")
    private String flowCode;

    @ApiModelProperty("任务编码")
    private String taskCode;

    @ApiModelProperty("运行状态")
    private Integer run;
}
