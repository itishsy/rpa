package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "policy_declare_column_rule_conclusion_detail")
@ApiModel("报盘字段规则结论动作表PO")
@Data
public class PolicyDeclareColumnRuleConclusionDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键id")
    protected Integer id;

    @Column
    @ApiModelProperty("结论id 对应表policy_declare_column_rule_conclusion id")
    private Integer conclusionId;

    @Column
    @ApiModelProperty("执行动作，1：显隐性，2：必选，3：赋值，4,：下拉选项，5：报盘不导出，6：回盘失败")
    private Integer type;

    @Column
    @ApiModelProperty("动作结果，如type为1时，1：显示，2：隐藏，如type为2时，2：必填，1：选填；如type为3时，则代表为赋值内容；如type为4时，则代表下拉选项内容，以逗号隔开；如type为6时，则代表回盘失败的原因")
    private String result;

}
