package com.seebon.rpa.entity.robot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("账户登录配置信息")
@Data
public class RobotTaskConfigDTO implements Serializable {

    @ApiModelProperty("表单配置的顺序")
    private Integer displayOrder;

    @ApiModelProperty("表单分组名称")
    private String groupName;

    @ApiModelProperty("表单配置信息")
    private List<RobotTaskArgsDTO> args;

}
