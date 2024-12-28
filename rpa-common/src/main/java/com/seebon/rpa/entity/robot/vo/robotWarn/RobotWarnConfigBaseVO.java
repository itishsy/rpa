package com.seebon.rpa.entity.robot.vo.robotWarn;

import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnRecipient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-02 12:13:59
 */
@ApiModel("rpa机器人预警基础配置信息VO")
@Data
public class RobotWarnConfigBaseVO extends RobotWarnConfigBase {

    @ApiModelProperty("rpa机器人预警信息接收人信息")
    List<RobotWarnRecipient> recipients;

}
