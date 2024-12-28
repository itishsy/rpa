package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1963", name = "获取方式")
public enum UpdateExcelSourceType {
    excel("来自excel表格"),
    json("来自json对象"),
    list("来自list数据集"),
    ;

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    UpdateExcelSourceType(String name) {
        this.name = name;
    }
}
