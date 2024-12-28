package com.seebon.rpa.entity.saas.vo.message;

import com.seebon.rpa.entity.saas.po.message.MessageUserReceiveSetting;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("客户用户消息接收配置信息VO")
@Data
public class MessageUserReceiveSettingVO extends MessageUserReceiveSetting {

    @ApiModelProperty("消息类型名称")
    private String messageTypeName;

    @ApiModelProperty("消息类型项目")
    private String warnTypeName;

    @ApiModelProperty("是否可选邮件方式，1：是，0：否")
    private Integer hasEmailWay;

    @ApiModelProperty("是否可选短信方式，1：是，0：否")
    private Integer hasSmsWay;

}
