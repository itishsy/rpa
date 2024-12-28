package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("定制报盘字段信息")
@Data
@Table(name = "policy_declare_custom_made_column_setting")
public class PolicyDeclareCustomMadeColumnSetting extends Identity {

    @Column
    @ApiModelProperty("uuid")
    private String uuid;

    @Column
    @ApiModelProperty("表policy_declare_base_custom_made_setting的uuid")
    private String declareBaseCustomMadeUuid;

    @Column
    @ApiModelProperty("申报列uuid(表policy_declare_column_setting的uuid)")
    private String declareColumnUuid;

    @Column
    @ApiModelProperty("报盘列名")
    private String declareColumnName;

    @Column
    @ApiModelProperty("字段类型（1：文本，2：下拉，3：数值，4：年月日，5：年月）")
    private Integer inputType;

    @Column
    @ApiModelProperty("数据源表，关联policy_declare_data_source_table code值")
    private String dataSourceTableCode;

    @Column
    @ApiModelProperty("数据源字段，关联policy_declare_data_source_item code值")
    private String dataSourceItemCode;

    @Column
    @ApiModelProperty("默认值")
    private String defaultValue;

    @Column
    @ApiModelProperty("导出格式")
    private String exportFormat;

    @Column
    @ApiModelProperty("备注")
    private String comment;

    @Column
    @ApiModelProperty("是否必填。1：不是；2：是")
    private Integer required;

}
