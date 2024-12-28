package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1973", name = "网站审核")
public enum WebsiteVerify {
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

    WebsiteVerify(String name) {
        this.name = name;
    }
}
