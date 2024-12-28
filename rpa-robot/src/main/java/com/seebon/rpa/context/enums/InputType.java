package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1966", name = "输入方式")
public enum InputType {

    input("输入"),
    keyboard("键盘敲入");


    String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    InputType(String name) {
        this.name = name;
    }

}
