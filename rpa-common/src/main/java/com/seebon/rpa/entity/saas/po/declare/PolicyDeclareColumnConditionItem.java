package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-13 17:03:46
 */
@Table(name = "policy_declare_column_condition_item")
@ApiModel("报盘字段条件明细信息")
@Data
public class PolicyDeclareColumnConditionItem extends Identity{

    @ApiModelProperty("报盘字段uuid")
    @Column
    private String declareColumnUuid;

    @ApiModelProperty("字段名")
    @Column
    private String declareColumnName;

    @ApiModelProperty("条件满足关系 1：等于，2：不等于，3：包含，4：不包含")
    @Column
    private Integer contrastMode;

    @ApiModelProperty("条件对比值")
    @Column
    private String contrastValue;
}
