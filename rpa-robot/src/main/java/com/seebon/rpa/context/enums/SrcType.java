package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1974", name = "链接类型")
public enum SrcType {

    action("action"),
    url("url");

    String name;

    SrcType(String name) {
        this.name = name;
    }

}
