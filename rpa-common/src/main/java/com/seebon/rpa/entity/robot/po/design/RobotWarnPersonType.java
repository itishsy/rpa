package com.seebon.rpa.entity.robot.po.design;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * TODO
 *
 * @author zjf
 * @describe 预警人员分类表
 * @date 2023/4/21 15:22
 */
@Data
@ApiModel("预警人员分类表")
@Table(name = "robot_warn_person_type")
public class RobotWarnPersonType{

    @Column
    @ApiModelProperty("id")
    private Integer id;

    @Column
    @ApiModelProperty("人员分类")
    private String personTypeName;

}
