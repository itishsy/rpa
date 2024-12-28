package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Data
@ApiModel(value = "预设表")
@Table(name = "robot_data_preset")
public class RobotDataPreset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "类型，枚举DataPresetEnum")
    private Integer type;

    @Column
    @ApiModelProperty(value = "内容")
    private String context;

    @Column
    @ApiModelProperty(value = "描述")
    private String comment;
}
