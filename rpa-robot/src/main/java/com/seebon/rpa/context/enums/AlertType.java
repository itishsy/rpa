package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-09 15:31:38
 */
@DataDictKey(value = "1038", name = "alter类型")
public enum AlertType {

    alert("消息提示"),
    confirm("确认消息");

    String name;
    AlertType(String name) {
        this.name = name;
    }
}
