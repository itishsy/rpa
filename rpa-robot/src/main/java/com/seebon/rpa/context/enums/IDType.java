package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1018", name = "唯一码类型")
public enum IDType {
    uuid("UUID"),
    seconds("当前秒"),
    timestamp("时间戳"),
    random("随机数");

    String name;
    IDType(String name){
        this.name = name;
    }
}
