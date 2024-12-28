package com.seebon.rpa.entity.saas.dto.system;

import lombok.Data;

@Data
public class WeixinResponse {
    private String msgid;
    private int errcode;
    private String errmsg;
}
