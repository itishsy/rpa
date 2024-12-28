package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-13 16:55:28
 */
@Table(name = "policy_declare_column_scope")
@ApiModel("报盘字段选择范围信息")
@Data
public class PolicyDeclareColumnScope extends Identity {

    @ApiModelProperty("报盘字段uuid")
    @Column
    private String declareColumnUuid;

    @ApiModelProperty("取值范围值")
    @Column
    private String scopeValue;

    @ApiModelProperty("是否显示 1：是，0：否")
    @Column
    private Integer isShow;
}
