package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("应用配置分组条件")
@Data
@Table(name = "robot_app_config_condition")
public class RobotAppConfigCondition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    @ApiModelProperty(value = "逻辑运算符")
    private String logicalOperator;

    @Column
    @ApiModelProperty(value = "条件左")
    private String conditionLeft;

    @Column
    @ApiModelProperty(value = "条件右")
    private String conditionRight;

    @Column
    @ApiModelProperty(value = "关系")
    private String relation;

    @Column
    @ApiModelProperty("添加条件的条件字段的id")
    private Integer relateConditionArgsId;

    @Column
    @ApiModelProperty(value = "分组明细id")
    private Integer argsDefineId;


}
