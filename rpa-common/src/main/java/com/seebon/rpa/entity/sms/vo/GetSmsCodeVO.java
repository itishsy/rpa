package com.seebon.rpa.entity.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("短信转发配置")
@Data
public class GetSmsCodeVO {
    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("短信内容关键字")
    private String smsKeyword;
}