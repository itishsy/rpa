package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("应用配置维护表单")
@Data
@Table(name = "robot_app_config_form")
public class RobotAppConfigForm implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "表单id")
    private String formCode;

    @Column
    @ApiModelProperty(value = "表单名")
    private String formName;


    @Column
    @ApiModelProperty(value = "机器人类型")
    private String robotType;

}
