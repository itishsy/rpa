package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1984", name = "win窗口操作")
public enum WindowActionType {
    maximizing("最大化窗口"),
    minimize("最小化窗口"),
    active("激活/聚焦窗口"),
    ;

    String name;

    WindowActionType(String name){
        this.name = name;
    }
}
