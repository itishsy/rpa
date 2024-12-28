package com.seebon.rpa.entity.robot.po.design;

/**
 * TODO
 *
 * @author zjf
 * @describe 预警人员信息表
 * @date 2023/4/21 17:42
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@ApiModel("预警人员信息表")
@Table(name = "robot_warn_person")
public class RobotWarnPerson{

    @Column
    @ApiModelProperty("id")
    private Integer id;

    @Column
    @ApiModelProperty("人员名字")
    private String name;

    @Column
    @ApiModelProperty("人员手机号")
    private String phoneNumber;

    @Column
    @ApiModelProperty("人员邮箱")
    private String email;
}
