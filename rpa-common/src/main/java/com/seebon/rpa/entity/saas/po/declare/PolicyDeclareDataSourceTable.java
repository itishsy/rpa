package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 14:14:50
 */
@ApiModel("报盘字段数据来源表")
@Data
@Table(name = "policy_declare_data_source_table")
public class PolicyDeclareDataSourceTable extends Identity {

    @Column
    @ApiModelProperty("数据源code")
    private String code;

    @Column
    @ApiModelProperty("数据源名称")
    private String name;

}
