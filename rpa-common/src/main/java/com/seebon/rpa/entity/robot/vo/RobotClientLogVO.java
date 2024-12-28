package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotClientLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@ApiModel("机器人客户端日志")
@Data
public class RobotClientLogVO extends RobotClientLog {
    @ApiModelProperty(value = "设备状态 1-正常 2-已废弃 3-需升级")
    private String statusName;
}