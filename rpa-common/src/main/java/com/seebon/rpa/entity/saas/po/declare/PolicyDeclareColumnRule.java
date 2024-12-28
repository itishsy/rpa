package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("报盘字段校验规则主表信息PO")
@Table(name = "policy_declare_column_rule")
@Data
public class PolicyDeclareColumnRule extends Identity {

    @Column
    @ApiModelProperty("申报列uuid(表policy_declare_column_setting的uuid)")
    private String declareColumnUuid;

    @Column
    @ApiModelProperty("条件间的关系，1：与，2：或")
    private Integer relation;

    @Column
    @ApiModelProperty("校验规则描述")
    private String comment;

}
