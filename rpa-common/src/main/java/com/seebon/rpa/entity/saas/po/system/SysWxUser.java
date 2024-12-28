package com.seebon.rpa.entity.saas.po.system;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "sys_wx_user")
@ApiModel("微信用户与手机号码关系表")
public class SysWxUser extends Identity {

    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("openId")
    private String openId;

    @ApiModelProperty("unionId")
    private String unionId;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("电子邮箱")
    private String email;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("客户id")
    private Integer custId;

}
