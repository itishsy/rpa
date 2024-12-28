package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1041", name = "Usb操作类型")
public enum UsbType {
    active("激活"),
    out("拔出"),
    ;

    String name;

    UsbType(String name) {
        this.name = name;
    }
}
