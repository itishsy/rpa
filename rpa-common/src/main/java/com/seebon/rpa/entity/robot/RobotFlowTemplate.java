package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel("机器人流程模板关系")
@Table(name = "robot_flow_template")
public class RobotFlowTemplate extends Identity {
    @Column
    @ApiModelProperty(value = "模板-流程编码")
    private String templateFlowCode;

    @Column
    @ApiModelProperty(value = "地区流程-流程编码")
    private String flowCode;
}
