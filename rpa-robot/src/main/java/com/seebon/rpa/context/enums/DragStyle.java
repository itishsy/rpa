package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1976", name = "匹配规则系数")
public enum DragStyle {

    FAST("快速"),
    NORMAL("常规"),
    GENTLE("温柔");

    String name;

    DragStyle(String name) {
        this.name = name;
    }

}
