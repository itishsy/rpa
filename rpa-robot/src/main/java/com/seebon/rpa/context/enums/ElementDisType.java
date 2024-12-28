package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1060", name = "页面元素展示状态")
public enum ElementDisType {

    display("显示"),
    notDisplay("不显示"),
    hasValue("有值")
    ;


    String name;

    ElementDisType(String name) {
        this.name = name;
    }
}
