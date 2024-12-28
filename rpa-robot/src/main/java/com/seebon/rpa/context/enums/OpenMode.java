package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1071", name = "打开模式")
public enum OpenMode {
    noTrace("无痕模式"),
    normal("正常模式"),
    ;

    String name;

    OpenMode(String name) {
        this.name = name;
    }
}
