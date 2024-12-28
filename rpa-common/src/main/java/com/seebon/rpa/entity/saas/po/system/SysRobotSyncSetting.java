package com.seebon.rpa.entity.saas.po.system;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

@Table(name = "sys_robot_sync_setting")
@Data
@ApiModel("客户机器人执行日志同步配置信息")
public class SysRobotSyncSetting extends Identity {

    @ApiModelProperty("同步状态，1：同步，0：不同步")
    private Integer syncStatus;

}
