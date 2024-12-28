package com.seebon.rpa.entity.saas.po.system;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "sys_wechat_official_account_setting")
@ApiModel("客户微信公众号开发信配置信息")
public class SysWechatOfficialAccountSetting extends Identity {

    @ApiModelProperty("1：公众号，2：企业微信")
    private Integer serviceType;

    @ApiModelProperty("1：使用自己的公众号，2：使用仕邦公众号， 3：万科企业微信，4：标准企业微信")
    @Column
    private Integer switchType;

    @ApiModelProperty("万科企业微信获取token接口地址")
    @Column
    private String accessTokenUrl;

    @ApiModelProperty("公众号appid或企业微信的accessKey")
    @Column
    private String appId;

    @ApiModelProperty("公众号开发者密码或企业微信的secretKey")
    @Column
    private String appSecret;

    @ApiModelProperty("企业微信企业应用的id")
    @Column
    private String agentId;

    @ApiModelProperty("企业微信企业应用的code")
    @Column
    private String agentCode;

    @ApiModelProperty("公众号消息模板")
    @Column
    private String msgTemplateId;

    @ApiModelProperty("公众号消息模板-消息名称字段")
    @Column
    private String msgTitleKey;

    @ApiModelProperty("公众号消息模板-申报单位字段")
    @Column
    private String orgNameKey;

    @ApiModelProperty("公众号消息模板-提醒时间字段")
    @Column
    private String timeKey;

    @ApiModelProperty("公众号消息模板-业务类型字段")
    @Column
    private String businessTypeKey;

    @ApiModelProperty("公众号消息模板-说明字段")
    @Column
    private String tipKey;


}
