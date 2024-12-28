package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1020", name = "遍历类型")
public enum IteratorType {
    list("List"),
    array("Array"),
    integer("Integer"),
    string("String");

    String name;
    IteratorType(String name){
        this.name = name;
    }
}
