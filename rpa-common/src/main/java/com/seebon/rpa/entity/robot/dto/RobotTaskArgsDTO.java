package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("账户登录配置详细信息")
@Data
public class RobotTaskArgsDTO implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("字段key")
    private String argsKey;

    @ApiModelProperty("字段名称")
    private String fieldName;

    @ApiModelProperty("字段类型")
    private String fieldType;

    @ApiModelProperty("字段值")
    private String argsValue;

    @ApiModelProperty("字段类型如是文件时，展示的文件名称")
    private String fileName;

}
