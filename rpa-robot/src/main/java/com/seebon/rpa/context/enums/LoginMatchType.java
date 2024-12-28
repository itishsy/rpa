package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1964", name = "登录判断类型")
public enum LoginMatchType {

    elementExists("元素存在"),
    elementTextContain("元素文本包含"),
    elementNoExists("元素不存在"),
    respExists("响应值存在"),
    respContain("响应值包含"),
    ;

    String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    LoginMatchType(String name) {
        this.name = name;
    }
}
