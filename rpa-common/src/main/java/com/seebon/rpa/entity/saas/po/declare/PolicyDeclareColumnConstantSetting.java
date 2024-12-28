package com.seebon.rpa.entity.saas.po.declare;


import com.seebon.excel.annotations.ExcelEntity;
import com.seebon.excel.annotations.ExcelProperty;
import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 15:29:16
 */
@ApiModel("报盘文件字段值映射信息")
@Data
@ExcelEntity
@Table(name = "policy_declare_column_constant_setting")
public class PolicyDeclareColumnConstantSetting extends Identity {

    @Column
    @ApiModelProperty("uuid")
    private String uuid;

    @Column
    @ApiModelProperty("申报列id(表t_policy_declare_colums_setting的uuid)")
    private String declareColumnUuid;

    @Column
    @ApiModelProperty("系统获取值")
    @ExcelProperty(value = "获取值", required = true, requiredMessage = "获取值不能为空")
    private String originalText;

    @Column
    @ApiModelProperty("需要显示到报盘模板的文本")
    @ExcelProperty(value = "导出值", required = true, requiredMessage = "导出值不能为空")
    private String showText;

    @Column
    @ApiModelProperty("是否对外展示 1--显示，2--不显示")
    private Integer outDisplay;

}
