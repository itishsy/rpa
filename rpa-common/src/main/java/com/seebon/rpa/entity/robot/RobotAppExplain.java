package com.seebon.rpa.entity.robot;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "robot_app_explain")
public class RobotAppExplain extends Identity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键Id")
    private Integer id;

    @Column
    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @Column
    @ApiModelProperty(value = "版本说明")
    private String comment;
}