package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1969", name = "输入方式")
public enum InputMethod {
    clickAndInput("单击并输入"),
    clickAndInputEnter("单击并输入后回车"),
    hasValueUnInput("有值不输入"),
    disabledUnInput("不可输入则跳过"),
    ;

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    InputMethod(String name) {
        this.name = name;
    }
}
