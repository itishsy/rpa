package com.seebon.rpa.entity.robot.dto.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.design.RobotFlowDesign;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 机器人应用的历史记录
 */
@Data
@Document(collection = "robot_app_history")
public class RobotAppHistory implements Serializable {
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty(value = "应用参数值")
    private String appArgs;

    @ApiModelProperty(value = "机器人编码")
    private String robotCode;

    @ApiModelProperty(value = "客户id")
    private Integer clientId;

    @ApiModelProperty(value = "地区")
    private String addrName;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "变更原因")
    private String changeReason;

    @ApiModelProperty(value = "版本")
    private String version;

    @ApiModelProperty(value = "版本说明")
    private String comment;

    @ApiModelProperty(value = "发布规则")
    private String rule;

    @ApiModelProperty(value = "执行计划")
    private String execCron;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "状态（运行中 1，失效2、作废3）")
    private int status;

    @ApiModelProperty(value = "并行执行（0-不可以，1-可以）")
    private Integer batchRun;

    @ApiModelProperty(value = "session维持（0：否，1-是）")
    private Integer sessionKeep;

    @ApiModelProperty("应用参数")
    private List<RobotArgsDefine> appArgsList;

    @ApiModelProperty("应用流程")
    private List<RobotFlow> flowList;

    @ApiModelProperty("流程步骤")
    private List<RobotFlowStep> flowStepList;

    @ApiModelProperty("信息配置条件")
    private List<RobotAppConfigCondition> configConditions;

    @ApiModelProperty("信息配置表单")
    private List<RobotAppConfigForm> configForms;

    @ApiModelProperty("信息配置分组")
    private List<RobotAppConfigGroup> configGroups;

    @ApiModelProperty("信息配置中间表")
    private List<RobotAppFormGroup> formGroupList;

    @ApiModelProperty("应用执行计划表")
    private List<RobotAppSchedule> schedules;

    @ApiModelProperty("任务执行计划表")
    private List<RobotTaskSchedule> taskSchedules;

    @ApiModelProperty("通用计划表--关联app")
    private List<RobotGeneralPlan> generalPlans;

    @ApiModelProperty("应用环境")
    private List<RobotAppEnv> appEnv;

    @ApiModelProperty("机器人流程模板关系")
    private List<RobotFlowTemplate> flowTemplate;

    @ApiModelProperty("机器人流程模板步骤关系")
    private List<RobotFlowTemplateStep> flowTemplateStep;

    @ApiModelProperty("机器人流程步骤参数")
    private List<RobotFlowStepArgs> flowStepArgs;

    @ApiModelProperty("机器人流程步骤流程")
    private List<RobotFlowStepFlow> flowStepFlow;

    @ApiModelProperty("客户机器人任务")
    private List<RobotTask> robotTasks;

    @ApiModelProperty("客户机器人任务参数")
    private List<RobotTaskArgs> robotTaskArgs;

    @ApiModelProperty("流程设计")
    private List<RobotFlowDesign> designList;

    private Integer createId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Integer updateId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private byte[] compressedData;
}
