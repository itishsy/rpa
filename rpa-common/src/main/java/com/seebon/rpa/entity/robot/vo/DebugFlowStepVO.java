package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotFlowStep;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DebugFlowStepVO {

    @ApiModelProperty("执行编码")
    private String executionCode;

    @ApiModelProperty("执行步骤")
    private List<RobotFlowStep> flowSteps;

    @ApiModelProperty("断点步骤编码")
    private List<String> debugStepCodes;

    @ApiModelProperty("执行参数")
    private Map<String, Object> flowArgs;

    @ApiModelProperty("浏览器参数")
    private Map<String, Object> chromeArgs;
}
