package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 13:40:10
 */
@ApiModel("报盘字段信息")
@Data
@Table(name = "policy_declare_column_setting")
public class PolicyDeclareColumnSetting extends Identity {

    @Column
    @ApiModelProperty("uuid")
    private String uuid;

    @Column
    @ApiModelProperty("表t_declare_base_setting uuid")
    private String declareBaseUuid;

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
    @ApiModelProperty("非首次收集字段，1：是，0：否")
    private Integer notFirstTime=0;

    @Column
    @ApiModelProperty("是否显示，1：是，0：否")
    private Integer isShow;

    @Column
    @ApiModelProperty("展示顺序")
    private Integer sort;

    @Column
    @ApiModelProperty("备注")
    private String comment;

    @Column
    @ApiModelProperty("是否必填。1：不是；2：是")
    private Integer required;

    @Column
    @ApiModelProperty("其它属于，字典值 10036，多个用逗号拼接")
    private String otherBelongTo;
}
