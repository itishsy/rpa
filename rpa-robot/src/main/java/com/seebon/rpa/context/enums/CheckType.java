package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1054", name = "应用检查类型")
public enum CheckType {
    app("应用是否打开"),
    element("元素是否存在"),
    ;

    String name;

    CheckType(String name){
        this.name = name;
    }
}
