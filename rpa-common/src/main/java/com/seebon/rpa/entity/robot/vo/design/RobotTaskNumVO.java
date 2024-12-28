package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.saas.po.declare.PolicyAddr;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("指定任务流程信息")
@Data
public class RobotTaskNumVO extends PolicyAddr {
    @ApiModelProperty("机器人数")
    private Integer num = 0;

    @ApiModelProperty("机器标识码")
    private String machineCode;

    @ApiModelProperty("运行机器人数")
    private Integer runNum = 0;

    @ApiModelProperty("运行机器标识码")
    private String runMachineCode;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("应用参数")
    private String appArgs;

    @ApiModelProperty("社保户")
    private String socialAccountNumber = "0";

    @ApiModelProperty("公积金户")
    private String accfundAccountNumber = "0";

    @ApiModelProperty("在职员工")
    private String registerNumber = "0";

    @ApiModelProperty("增减员运行数")
    private String declareNumber = "0";

    @ApiModelProperty("增减员运行成功数")
    private String declareSuccessNumber = "0";

    @ApiModelProperty("任务调度平均时长-分钟")
    private String avgDispatchTime = "0";

    @ApiModelProperty("任务执行平均时长-分钟")
    private String avgFinishTime = "0";
}
