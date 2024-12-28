package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("报盘字段规则条件明细表PO")
@Table(name = "policy_declare_column_rule_condition_detail")
@Data
public class PolicyDeclareColumnRuleConditionDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    protected Integer id;

    @Column
    @ApiModelProperty("申报字段规则id(表policy_declare_column_rule的id)")
    private Integer declareColumnRuleId;

    @Column
    @ApiModelProperty("条件字段名")
    private String declareColumnName;

    @Column
    @ApiModelProperty("条件字段属性，1：值，2：年月，3：年，4：天数-距当日")
    private Integer valueType;

    @Column
    @ApiModelProperty("条件满足关系 1：等于，2：不等于，3：包含，4：不包含，5：大于，6：大于等于，7：小于，8：小于等于，9：为空，10：不为空")
    private Integer contrastMode;

    @Column
    @ApiModelProperty("比较对象类型，1：输入值，2：报盘字段，3：其他字段")
    private Integer compareType;

    @Column
    @ApiModelProperty("比较对象，如compare_type等于1：内容为输入的值，如compare_type等于2：内容为报盘字段名，如compare_type等于3：其他字段名")
    private String compareObj;

    @Column
    @ApiModelProperty("比较属性,1:值，2：年月，3：年")
    private Integer compareAttribute;

    @Column
    @ApiModelProperty("条件结论")
    private String conclusion;

}
