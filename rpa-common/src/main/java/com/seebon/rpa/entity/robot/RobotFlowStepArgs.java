package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("机器人流程步骤参数")
@Data
@Table(name = "robot_flow_step_args")
public class RobotFlowStepArgs extends Identity {
    @Column
    @ApiModelProperty("流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty("代码块编码")
    private String groupCode;

    @Column
    @ApiModelProperty("步骤编码")
    private String stepCode;

    @Column
    @ApiModelProperty("目标参数值")
    private String targetArgs;

    @Column
    @ApiModelProperty("行为参数值")
    private String actionArgs;
}
