package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RobotTaskSessionKeepVO extends RobotTaskSessionKeep {
    private String appName;
    private String orgName;
    private String accountNumber;
    private String accountAndOrgName;
    private String customerName;
    private String taskName;
    private String declareSystemName;
    private String phoneNumber;
    private String loginType;
}
