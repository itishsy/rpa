package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1070", name = "操作类型")
public enum OppAppType {
    CLICK("点击"),
    CLEAR("清空"),
    SEND("输入")
    ;
    String  name;
    OppAppType(String name){
        this.name = name;
    }

}
