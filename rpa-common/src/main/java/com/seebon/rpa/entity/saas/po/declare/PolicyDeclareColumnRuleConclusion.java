package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "policy_declare_column_rule_conclusion")
@ApiModel("报盘字段规则结论信息PO")
@Data
public class PolicyDeclareColumnRuleConclusion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    protected Integer id;

    @Column
    @ApiModelProperty("申报列规则id(表policy_declare_column_rule的id)")
    private Integer declareColumnRuleId;

    @Column
    @ApiModelProperty("校验板块 1：入数据，2：导报盘")
    private Integer module;

    @Column
    @ApiModelProperty("申报系统，对应字典值10007")
    private String tplTypeCode;

    @Column
    @ApiModelProperty("作用对象 1：报盘字段，2：数据")
    private Integer targetAudience;

    @Column
    @ApiModelProperty("字段名，作用对象为报盘字段时的字段名称")
    private String declareColumnName;

    @Column
    @ApiModelProperty("结论")
    private String conclusion;

}
