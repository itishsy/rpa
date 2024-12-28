package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1979", name = "是否退出流程")
public enum QuitFlow {

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

    QuitFlow(String name) {
        this.name = name;
    }
}
