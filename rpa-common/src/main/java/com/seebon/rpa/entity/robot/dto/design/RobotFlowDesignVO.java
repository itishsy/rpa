package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.vo.RobotFlowStepVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器人流程设计
 */
@Data
public class RobotFlowDesignVO {
    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty(value = "是否模板(0：否,1：是)")
    private Integer templateType;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "流程code")
    private String flowCode;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @ApiModelProperty(value = "步骤code")
    private String stepCode;

    @ApiModelProperty(value = "步骤名称")
    private String stepName;

    @ApiModelProperty(value = "模板流程code")
    private String templateFlowCode;

    @ApiModelProperty(value = "流程json")
    private String data;

    @ApiModelProperty(value = "步骤steps")
    private List<RobotFlowStepVO> steps;

    @ApiModelProperty(value = "创建人Id")
    private Integer createId;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新者Id")
    private Integer updateId;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;
}
