package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-13 16:59:39
 */
@Table(name = "policy_declare_column_condition")
@ApiModel("报盘字段条件主表")
@Data
public class PolicyDeclareColumnCondition extends Identity {

    @ApiModelProperty("报盘字段uuid")
    @Column
    private String declareColumnUuid;

    @ApiModelProperty("关系 1：与，2：或")
    @Column
    private Integer relation;

    @ApiModelProperty("是否显示 1：是，0：否")
    @Column
    private Integer isShow;

    @ApiModelProperty("是否必填 2：是，1：否")
    @Column
    private Integer required;

    @ApiModelProperty("结论")
    @Column
    private String conclusion;

    @ApiModelProperty("满足条件的默认值")
    @Column
    private String conditionValue;

}
