package com.seebon.rpa.entity.saas.vo.system.robot;

import com.seebon.rpa.entity.saas.po.system.robot.RobotLoginNoticeItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("机器人登录推送消息明细表VO")
@Data
public class RobotLoginNoticeItemVO extends RobotLoginNoticeItem {

    @ApiModelProperty("二维码链接地址")
    private String imgHttpUrl;

    @ApiModelProperty("数据发起类型")
    private String dataType;

}
