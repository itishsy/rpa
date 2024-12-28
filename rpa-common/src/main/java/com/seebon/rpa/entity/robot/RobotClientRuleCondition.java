package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@ApiModel(value = "任务指定盒子条件规则明细表")
@Table(name = "robot_client_rule_condition")
public class RobotClientRuleCondition implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @Column
    @ApiModelProperty(value = "规则主表id(表robot_task_rule的id)")
    private Integer ruleId;

    @Column
    @ApiModelProperty(value = "条件字段(1-应用名称、2-申报账户、3-申报系统、4-事项、5-登录方式)")
    private Integer columnType;

    @Column
    @ApiModelProperty(value = "条件满足关系 1：等于，2：不等于，3：包含，4：不包含")
    private Integer contrastMode;

    @Column
    @ApiModelProperty(value = "条件字段值")
    private String columnValue;

    @Column
    @ApiModelProperty(value = "条件结论")
    private String conclusion;
}
