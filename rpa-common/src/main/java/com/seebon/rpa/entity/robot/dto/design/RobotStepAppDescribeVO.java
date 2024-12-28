package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author hao
 * @Date 2022/9/22 11:22
 * @Version 1.0
 **/
@Data
public class RobotStepAppDescribeVO {
    private String appName;

    private String businessType;

    private String flowName;

    private String flowType;

    private String relationFlowCode;

    private String relationAppNameAndFlowName;
}
