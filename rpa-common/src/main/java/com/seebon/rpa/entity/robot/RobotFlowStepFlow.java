package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("机器人流程步骤流程")
@Data
@Table(name = "robot_flow_step_flow")
public class RobotFlowStepFlow extends Identity {

    @Column
    @ApiModelProperty("模板-流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty("步骤编码")
    private String stepCode;

    @Column
    @ApiModelProperty("子流程-流程编码")
    private String childFlowCode;
}
