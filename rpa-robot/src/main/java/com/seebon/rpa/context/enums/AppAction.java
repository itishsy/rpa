package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1051", name = "应用操作")
public enum AppAction {
    start("启动"),
    close("关闭"),
    open("打开");

    String name;

    AppAction(String name) {
        this.name = name;
    }
}
