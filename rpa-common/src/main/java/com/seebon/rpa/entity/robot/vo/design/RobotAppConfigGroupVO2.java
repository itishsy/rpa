package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.RobotAppConfigGroup;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@ApiModel("应用配置分组VO2")
@Data
@Accessors(chain = true)
//@Table(name = "robot_app_config_group")
public class RobotAppConfigGroupVO2 extends RobotAppConfigGroup {

    @ApiModelProperty(value="应用配置分组明细")
    private List<RobotArgsDefine> robotAppConfigGroupItems;
}
