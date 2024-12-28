package com.seebon.rpa.entity.robot;


import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "表单和应用类型中间表")
@Table(name = "robot_input_type")
public class RobotInputType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @ApiModelProperty(value = "表单名称")
    private String inputName;

    @Column
    @ApiModelProperty(value = "表单id")
    private String inputId;

    @Column
    @ApiModelProperty(value = "应用类型")
    private String type;

}
