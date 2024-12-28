package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1975", name = "匹配规则系数")
public enum MethodType {

    TM_CCOEFF("TM_CCOEFF"),
    TM_SQDIFF("TM_SQDIFF");

    String name;

    MethodType(String name) {
        this.name = name;
    }

}
