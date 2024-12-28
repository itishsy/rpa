package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.RobotArgsDefine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel("应用配置分组明细VO")
@Data
@Accessors(chain = true)
public class RobotAppConfigGroupItemVO extends RobotArgsDefine {

    @ApiModelProperty("机器人应用代码")
    private String appCode;

    private String robotType;

    private Integer groupId;

    private Integer formId;

}
