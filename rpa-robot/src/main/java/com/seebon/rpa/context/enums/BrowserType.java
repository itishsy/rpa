package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1005", name = "浏览器类型")
public enum BrowserType {
    chrome("谷歌"),
    ie("IE"),
    chrome360("360"),
    firefox("火狐"),
    edge("Edge");

    String name;
    BrowserType(String name){
        this.name = name;
    }
}
