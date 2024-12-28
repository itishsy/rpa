package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author hao
 * @Date 2022/10/26 11:43
 * @Version 1.0
 **/
@Table(name = "robot_app_form_group")
@ApiModel("表单与组中间表")
@Data
public class RobotAppFormGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String appCode;

    @Column
    private Integer formId;

    @Column
    private Integer groupId;

    @Column
    private Integer isDelete;
}
