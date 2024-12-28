package com.seebon.rpa.entity.saas.po.system;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "sys_email_setting")
@ApiModel("客户邮件发件箱信息")
public class SysEmailSetting extends Identity {

    @Column
    @ApiModelProperty(value="邮箱账号")
    private String messMailUser;

    @Column
    @ApiModelProperty(value="邮箱账号密码")
    private String messMailPasswd;

    @Column
    @ApiModelProperty(value="发件服务器地址")
    private String messMailServiceHost;

}
