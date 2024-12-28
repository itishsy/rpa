package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@ApiModel("应用配置分组")
@Data
@Table(name = "robot_app_config_group")
public class RobotAppConfigGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "分组名称")
    private String groupName;


    @Column
    @ApiModelProperty(value = "状态 0:启用 1:禁用")
    private String status;

    @Column
    @ApiModelProperty(value = "机器人应用类型")
    private String robotType;


}
