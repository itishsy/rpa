package com.seebon.rpa.entity.saas.vo.employee;


import com.seebon.rpa.entity.saas.po.declare.PolicyDeclareColumnConstantSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


/**
 * @author lsh
 * @dateTime 2022-04-22
 */
@Data
@ApiModel("投保-查询动态字段VO")
public class InsuredDynamicVo {

    @ApiModelProperty("字段code")
    private String codeName;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("字段类型 0-文本，1-下拉，2-自增长")
    private Integer type;

    @ApiModelProperty("存储所在表名")
    private String tableName;

    @ApiModelProperty("存储所在表字段名")
    private String columnName;

    @ApiModelProperty("格式code")
    private String exportFormat;

    @ApiModelProperty("显示顺序")
    private Integer sort;

    @ApiModelProperty("是否输入字段 1-是，0-否")
    private Integer input;

    @ApiModelProperty("字段类型（1：文本，2：下拉，3：数值，4：年月日，5：年月）")
    private Integer inputType;

    @ApiModelProperty("是否取值字段 1-是，0-否")
    private Integer output;

    @ApiModelProperty("是否固定字段 1-是，0-否")
    private Integer fixed;

    @ApiModelProperty("查询的结果值")
    private String value;

    @ApiModelProperty("注解")
    private String comment;

    @ApiModelProperty("查询的结果值范围")
    private List<String> values;

    @ApiModelProperty("2:必填  1：不必填")
    private String required;

    private String code;

    private String uuid;

    @ApiModelProperty("区分别动态字段里是否有自定义字段，不需要页面作展示")
    private String tableCode;

    private List<PolicyDeclareColumnConstantSetting> bases;
}
