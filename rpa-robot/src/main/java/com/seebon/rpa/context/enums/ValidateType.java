package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1959", name = "推送方式")
public enum ValidateType {
    sms("短信", "10021001"),
    qrCode("二维码登录", "10021002"),
    faceCode("人脸二维码", "10021003"),
    dynamicCode("动态码", "10021001"),
    ;

    String name;

    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ValidateType(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
