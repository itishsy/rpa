package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("驾驶仓-机器人数据概括")
@Data
public class ServiceDataStatisticsDTO implements Serializable {

    @ApiModelProperty("开通城市")
    private String cityNumber = "0";

    @ApiModelProperty("社保户")
    private String socialAccountNumber = "0";

    @ApiModelProperty("公积金户")
    private String accfundAccountNumber = "0";

    @ApiModelProperty("在职员工")
    private String registerNumber = "0";

    @ApiModelProperty("增减员运行数")
    private String declareNumber = "0";

    @ApiModelProperty("总费用")
    private String totalFee = "0";

    @ApiModelProperty("增减员运行成功数")
    private String declareSuccessNumber = "0";

    @ApiModelProperty("正确率")
    private String successRate = "100%";

    @ApiModelProperty("人均完成时间-分钟")
    private String avgTime = "0";

    @ApiModelProperty("任务调度平均时长-分钟")
    private String avgDispatchTime = "0";

    @ApiModelProperty("任务执行平均时长-分钟")
    private String avgFinishTime = "0";

    @ApiModelProperty("RPA累计运行时长-分钟")
    private String robotExecTotalTime = "0";

}
