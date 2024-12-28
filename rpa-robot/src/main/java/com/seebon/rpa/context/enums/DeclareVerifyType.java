package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1960", name = "获取方式")
public enum DeclareVerifyType {
    excel("excel表格"),
    json("json对象"),
    jsonPdf("json对象pdf文件"),
    customPath("文件路径"),
    ;

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DeclareVerifyType(String name) {
        this.name = name;
    }
}
