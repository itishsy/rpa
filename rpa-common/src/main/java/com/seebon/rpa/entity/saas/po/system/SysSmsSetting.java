package com.seebon.rpa.entity.saas.po.system;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "sys_sms_setting")
@ApiModel("客户短信服务信息")
public class SysSmsSetting extends Identity {

    @Column
    @ApiModelProperty(value="短信服务地址")
    private String serviceUrl;

    @Column
    @ApiModelProperty(value="接口请求类型，post、get")
    private String methodType;

    @Column
    @ApiModelProperty(value="HTTP内容类型")
    private String contentType;

    @Column
    @ApiModelProperty(value="xml模版，如contentType为text/xml; charset=utf-8必填")
    private String xmlResource;

    @Column
    @ApiModelProperty(value="短信接收手机号接口参数名")
    private String phoneKey;

    @Column
    @ApiModelProperty(value="短信内容接口参数名")
    private String smsContentKey;

    @Column
    @ApiModelProperty(value="发送url")
    private String smsReceiveUrl;

    @Column
    @ApiModelProperty(value = "使用方式：1-自有接口，2-spfcore")
    private Integer useType;

    @Column
    @ApiModelProperty(value = "账号")
    private String userName;

    @Column
    @ApiModelProperty(value = "密码")
    private String password;

    @Column
    @ApiModelProperty(value = "签名")
    private String sign;
}
