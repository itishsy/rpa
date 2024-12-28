package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotClient;
import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.RobotClientLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/9/5 17:52
 * @Version 1.0
 **/
@Data
public class RobotClientVO extends RobotClient{
    private List<RobotClientApp> robotClientApps;

    @ApiModelProperty(value="客户名称")
    private String clientName;

    @ApiModelProperty(value="设备状态")
    private String statusName;

    private List<RobotClientAppVO> robotClientAppVOS;

    @ApiModelProperty("累计申报人数")
    private Integer totalDeclareNumber;

    @ApiModelProperty("机器人设备名字")
    private String seebotName;

    @ApiModelProperty("正在执行的账户")
    private String executingAccount;

    private List<RobotClientLog> logList;

    private boolean flag=true;
}

