package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-09 15:35:27
 */
@DataDictKey(value = "1039", name = "操作类型")
public enum OperationType {

    confirm("确定"),
    cancel("取消");

    String name;

    OperationType(String name) {
        this.name = name;
    }
}
