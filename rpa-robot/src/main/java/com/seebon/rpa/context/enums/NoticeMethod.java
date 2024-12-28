package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-02-13 10:26:47
 */
@DataDictKey(value = "1043", name = "消息通知方法")
public enum NoticeMethod {

    email("邮件消息"),
    sms("短信消息");

    String name;

    NoticeMethod(String name) {
        this.name = name;
    }
}
