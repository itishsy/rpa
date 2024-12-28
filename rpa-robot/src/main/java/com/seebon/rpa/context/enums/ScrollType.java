package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1044", name = "浏览器操作")
public enum ScrollType {
    bottom("向下滚动"),
    top("向上滚动"),
    ;

    String name;

    ScrollType(String name) {
        this.name = name;
    }
}
