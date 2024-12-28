package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.RobotFlowStepFlow;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器人流程设计
 */
@Data
public class RobotFlowDesignSyncVO {

    @ApiModelProperty(value = "版本说明")
    private String comment;

    @ApiModelProperty(value = "流程编码")
    private List<String> flowCodes;

    @ApiModelProperty("流程设计")
    private List<RobotFlowDesign> designList;

    @ApiModelProperty("应用流程")
    private List<RobotFlow> flowList;

    @ApiModelProperty("流程步骤")
    private List<RobotFlowStep> flowStepList;

    @ApiModelProperty("机器人流程步骤流程")
    private List<RobotFlowStepFlow> flowStepFlow;
}
