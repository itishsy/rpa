package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1010", name = "HTTP响应数据类型")
public enum HttpDataType {
    map("JSON对象"),
    str("字符串"),
    list("集合");

    String name;
    HttpDataType(String name){
        this.name = name;
    }
}
