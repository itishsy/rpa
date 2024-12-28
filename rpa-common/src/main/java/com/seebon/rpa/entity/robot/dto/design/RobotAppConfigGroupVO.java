package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel("应用配置分组VO")
@Data
@Accessors(chain = true)
public class RobotAppConfigGroupVO {

    private Integer id;

    private String groupName;

    private Integer groupId;

    private String appCode;

    private Integer formId;

    private String robotType;
}
