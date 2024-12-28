package com.seebon.rpa.entity.robot.dto;

import com.google.common.collect.Lists;
import com.seebon.rpa.entity.robot.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RobotAppDTO extends RobotApp {

    private Integer clientId;

    @ApiModelProperty("流程")
    private List<RobotFlow> robotFlows = Lists.newArrayList();

    @ApiModelProperty("流程步骤")
    private List<RobotFlowStep> robotFlowSteps = Lists.newArrayList();

    @ApiModelProperty("应用配置分组条件")
    private List<RobotAppConfigCondition> configConditions = Lists.newArrayList();

    @ApiModelProperty("应用配置维护表单")
    private List<RobotAppConfigForm> configForms = Lists.newArrayList();

    @ApiModelProperty("应用配置分组")
    private List<RobotAppConfigGroup> configGroups = Lists.newArrayList();

    @ApiModelProperty("表单与组中间表")
    private List<RobotAppFormGroup> formGroupList = Lists.newArrayList();

    @ApiModelProperty("应用执行计划")
    private List<RobotAppSchedule> scheduleList = Lists.newArrayList();

    @ApiModelProperty("任务执行计划")
    private List<RobotTaskSchedule> taskScheduleList = Lists.newArrayList();

    @ApiModelProperty("机器人参数定义")
    private List<RobotArgsDefine> robotArgsDefines = Lists.newArrayList();

    @ApiModelProperty("通用计划表")
    private List<RobotGeneralPlan> generalPlanList = Lists.newArrayList();

    @ApiModelProperty("客户机器人任务")
    private List<RobotTask> robotTaskList = Lists.newArrayList();

    @ApiModelProperty("客户机器人任务参数")
    private List<RobotTaskArgs> robotTaskArgsList = Lists.newArrayList();

    @ApiModelProperty("应用环境")
    private List<RobotAppEnv> robotAppEnv = Lists.newArrayList();

    @ApiModelProperty("机器人流程模板关系")
    private List<RobotFlowTemplate> robotFlowTemplate = Lists.newArrayList();

    @ApiModelProperty("机器人流程模板步骤关系")
    private List<RobotFlowTemplateStep> robotFlowTemplateStep = Lists.newArrayList();

    @ApiModelProperty("机器人流程步骤参数")
    private List<RobotFlowStepArgs> robotFlowStepArgs = Lists.newArrayList();

    @ApiModelProperty("机器人流程步骤流程")
    private List<RobotFlowStepFlow> robotFlowStepFlow = Lists.newArrayList();
}
