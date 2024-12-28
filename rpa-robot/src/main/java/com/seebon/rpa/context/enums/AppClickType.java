package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1052", name = "应用点击方式")
public enum AppClickType {
    click("单击"),
    doubleClick("双击"),
    mouseClick("鼠标点击"),
    ;

    String name;

    AppClickType(String name){
        this.name = name;
    }
}
