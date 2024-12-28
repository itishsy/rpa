package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("手动任务执行情况")
@Data
public class RobotCommandVO extends RobotCommand {

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("单位社保号或公积金号")
    private String accountNumber;

    @ApiModelProperty("执行状态名称")
    private String statusName;

}
