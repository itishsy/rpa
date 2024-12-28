package com.seebon.rpa.entity.saas.vo.system.robot;

import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.po.system.robot.RobotLoginNoticeTemplate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("机器人登录提醒模板配置信息VO")
@Data
public class RobotLoginNoticeTemplateVO extends RobotLoginNoticeTemplate {

    @ApiModelProperty("系统类型名称")
    private String tplTypeName;

    @ApiModelProperty("推送方式名称")
    private String validateTypeName;

    @ApiModelProperty("语音模板内容")
    private String voiceContent;

    @ApiModelProperty("短信模板内容")
    private String smsContent;

    @ApiModelProperty("应用绑定的客户")
    private List<CustomerBase> customerList;

}
