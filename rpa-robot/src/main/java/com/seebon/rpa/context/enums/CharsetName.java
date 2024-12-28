package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1962", name = "字符编码")
public enum CharsetName {

    UTF8("UTF-8"),
    GBK("GBK"),
    ;

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    CharsetName(String name) {
        this.name = name;
    }
}
