package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.RobotAppConfigCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("应用配置分组条件VO")
@Data
public class RobotAppConfigConditionVO extends RobotAppConfigCondition {
    @ApiModelProperty("机器人应用代码")
    private String appCode;

    private Integer showOrHidden;

}
