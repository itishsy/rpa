package com.seebon.rpa.entity.robot.dto.robotWarn;

import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnRecipient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-26 14:36:10
 */
@ApiModel("预警信息转发配置信息")
@Data
public class RobotWarnMsgForwardDTO {

    @ApiModelProperty("转发消息的id")
    private Integer msgId;

    @ApiModelProperty("转发接收人信息")
    List<RobotWarnRecipient> recipients;

}
