package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;


@DataDictKey(value = "1919", name = "WebExcelType插入类型")
public enum WebExcelType {

    input("input"),

    select("select"),

    li("li");

    String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    WebExcelType(String name){
        this.name = name;
    }

}
