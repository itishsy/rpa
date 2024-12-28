package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotFlowStep;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("机器人步骤")
@Data
public class RobotFlowStepVO extends RobotFlowStep {

    private List<RobotFlowStepArgsVO> actionArgsVOS;

    private List<RobotFlowStepArgsVO> targetArgsVOS;

    @ApiModelProperty("成功后跳转到步骤")
    private String trueSkipTo;
}