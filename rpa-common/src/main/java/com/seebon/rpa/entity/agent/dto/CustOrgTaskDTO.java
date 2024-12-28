package com.seebon.rpa.entity.agent.dto;

import com.seebon.rpa.entity.robot.RobotFlow;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CustOrgTaskDTO implements Serializable {

    private Integer clientId;

    private String taskCode;

    private String taskName;

    private String machineCode;

    private String appCode;

    private String appArgs;

    // 申报账户状态  0--暂停  1--启动
    private Integer status;

    // 原因备注
    private String comment;

    private List<RobotFlow> flows;

}
