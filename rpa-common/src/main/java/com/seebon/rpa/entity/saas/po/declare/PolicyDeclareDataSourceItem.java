package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 14:20:40
 */
@ApiModel("报盘设置数据源字段信息")
@Data
@Table(name = "policy_declare_data_source_item")
public class PolicyDeclareDataSourceItem extends Identity {

    @ApiModelProperty("字段code")
    @Column
    private String code;

    @ApiModelProperty("字段名称")
    @Column
    private String name;

    @ApiModelProperty("字段类型 0-文本，1-下拉，2-自增长")
    @Column
    private Integer type;

    @ApiModelProperty("所在表名")
    @Column
    private String tableName;

    @ApiModelProperty("所在表字段名")
    @Column
    private String columnName;

    @ApiModelProperty("是否输入字段 1-是，0-否")
    @Column
    private Integer input;

    @ApiModelProperty("是否取值字段 1-是，0-否")
    @Column
    private Integer output;

    @ApiModelProperty("是否固定字段 1-是，0-否")
    @Column
    private Integer fixed;

    @ApiModelProperty("数据源code")
    @Column
    private String tableCode;

    @ApiModelProperty("排序编号")
    @Column
    private Integer sort;

    @ApiModelProperty("适用业务，1：社保，2：公积金，0：通用")
    @Column
    private Integer businessType;


}
