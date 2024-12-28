package com.seebon.rpa.entity.saas.dto.system;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 微信公众号的传递消息体
 * @param
 */
@Data
public class WechatTemplate {
    private String touser;
    private String template_id;
    private String url;
    private MiniprogramParam miniprogram;
    private Map<String, TemplateData> data;
    private WeixinResponse response;
    private Date callTime;
    private String procsId;
}
