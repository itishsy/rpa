package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1985", name = "是否开启拦截")
public enum ApiRespOpenIntercept {

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

    ApiRespOpenIntercept(String name) {
        this.name = name;
    }
}
