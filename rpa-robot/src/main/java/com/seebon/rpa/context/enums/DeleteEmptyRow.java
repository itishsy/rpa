package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1965", name = "剔除补全为空数据")
public enum DeleteEmptyRow {

    TRUE("是"),
    FALSE("否"),
    ;

    String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DeleteEmptyRow(String name) {
        this.name = name;
    }
}
