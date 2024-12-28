package com.seebon.rpa.entity.saas.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "发送手机验证码前端传入对象")
public class MobileCodeDTO {

    @ApiModelProperty("手机验证码用途类型，register：注册，login：登录，forgetpwd：忘记密码，changepwd：修改密码，changephone：更改手机号")
    private String type;

    @ApiModelProperty("用户")
    private String username;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("唯一码")
    private String stamp;

    @ApiModelProperty("图片校验码")
    private String imageCode;

    @ApiModelProperty("手机校验码")
    private String msgCode;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("密码")
    private String checkPassword;


}
