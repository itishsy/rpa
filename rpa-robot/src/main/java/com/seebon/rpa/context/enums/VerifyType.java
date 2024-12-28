package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-04 10:34:42
 */
@DataDictKey(value = "1032", name = "验证码类型")
public enum VerifyType {

    drag("滑块验证"),
    verifyCode("字符验证"),
    QRCode("二维码"),
    smsCode("短信验证码");


    String name;
    VerifyType(String name){
        this.name = name;
    }

}
