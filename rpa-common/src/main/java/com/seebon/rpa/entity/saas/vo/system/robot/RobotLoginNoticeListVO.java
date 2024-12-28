package com.seebon.rpa.entity.saas.vo.system.robot;

import com.seebon.rpa.entity.saas.po.system.robot.RobotLoginNoticeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("机器人登录推送消息列表VO")
@Data
public class RobotLoginNoticeListVO extends RobotLoginNoticeList {

    @ApiModelProperty("客户名称")
    private String custName;

    @ApiModelProperty("发送详情信息")
    private List<RobotLoginNoticeItemVO> items;

    @ApiModelProperty("失败类型")
    private String failType;

    @ApiModelProperty("验证方式")
    private String MsgType;

}
