package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel("机器人流程模板步骤关系")
@Table(name = "robot_flow_template_step")
public class RobotFlowTemplateStep extends Identity {
    @Column
    @ApiModelProperty(value = "模板-流程编码")
    private String templateFlowCode;

    @Column
    @ApiModelProperty(value = "模板-流程编码")
    private String templateStepCode;

    @Column
    @ApiModelProperty(value = "主流程-流程编码")
    private String mainFlowCode;

    @Column
    @ApiModelProperty(value = "地区流程-流程编码")
    private String flowCode;
}
