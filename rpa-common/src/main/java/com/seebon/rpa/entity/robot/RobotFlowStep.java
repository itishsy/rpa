package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;


@ApiModel("机器人步骤")
@Data
@Table(name = "robot_flow_step")
public class RobotFlowStep extends Identity {
    public static final String SKIP_TO = "skipTo";
    public static final String FAILED_SKIP_TO = "failedSkipTo";

    @Column
    @ApiModelProperty("流程编码")
    private String flowCode;

    @Column
    @ApiModelProperty("代码块编码")
    private String groupCode;

    @Column
    @ApiModelProperty("步骤编码")
    private String stepCode;

    @Column
    @ApiModelProperty("步骤名称")
    private String stepName;

    @Column
    @ApiModelProperty("指令编码")
    private String actionCode;

    @Column
    @ApiModelProperty("行为参数值")
    private String actionArgs;

    @Column
    @ApiModelProperty("目标参数值")
    private String targetArgs;

    @Column
    @ApiModelProperty("执行序号")
    private Integer number;

    @Column
    @ApiModelProperty("显示层级")
    private Integer level;

    @Column
    @ApiModelProperty("状态：1启用，0禁用。")
    private Integer status;

    @Column
    @ApiModelProperty("失败重试次数")
    private Integer failedRetry;

    @Column
    @ApiModelProperty("忽略执行失败：1忽略，0中断（缺省）。")
    private Integer failedStrategy;

    @Column
    @ApiModelProperty("失败后跳转到步骤")
    private String failedSkipTo;

    @Column
    @ApiModelProperty("成功后跳转到步骤")
    private String skipTo;

    @Column
    @ApiModelProperty("为false跳转步(判断指令才有效)")
    private String falseSkipTo;

    @Column
    @ApiModelProperty("跳过此步骤的条件")
    private String skipCondition;

    @Column
    @ApiModelProperty("执行前等待时间(s), 空为默认随机时间")
    private Integer waitBefore;

    @Column
    @ApiModelProperty("执行后等待时间(s), 空为不等待")
    private Integer waitAfter;

    @Column
    @ApiModelProperty("执行超时时间")
    private Integer timeout;

    @Column
    @ApiModelProperty("步骤类型：1-模板,2-自定义")
    private Integer type;

    @Column
    @ApiModelProperty("是否放开编辑（0：否，1：是）")
    private Integer openEdit;

    @Column
    @ApiModelProperty("最大执行次数")
    private Integer maxExecuteNum;
}
