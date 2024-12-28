package com.seebon.rpa.entity.robot.dto.design;

import com.seebon.rpa.entity.robot.RobotFlowStep;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 机器人流程设计
 */
@Data
@Document(collection = "robot_flow_design")
public class RobotFlowDesign implements Serializable {
    @Id
    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty(value = "是否模板(0：否,1：是)")
    private Integer templateType;

    @ApiModelProperty(value = "流程code")
    private String flowCode;

    @ApiModelProperty(value = "流程名称")
    private String flowName;

    @ApiModelProperty(value = "流程json")
    private String data;

    @ApiModelProperty(value = "步骤steps")
    private List<RobotFlowStep> steps;

    @ApiModelProperty(value = "创建人Id")
    private Integer createId;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新者Id")
    private Integer updateId;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;
}
