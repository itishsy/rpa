package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 11:01:11
 */
@ApiModel(value="报盘设置关联文件信息")
@Table(name = "policy_declare_base_tpl")
@Data
public class PolicyDeclareBaseTpl extends Identity {

    @Column
    @ApiModelProperty("报盘设置主表uuid")
    private String declareBaseUuid;

    @Column
    @ApiModelProperty("单位社保号/单位公积金号")
    private String comAccountNumber;


    @Column
    @ApiModelProperty("关联文件名称")
    private String tplName;


    @Column
    @ApiModelProperty("关联文件路径")
    private String tplPath;

    @Column
    @ApiModelProperty("模板类型")
    private String tplType;

    @Column
    @ApiModelProperty("模版分支类型")
    private String operationType;

}
