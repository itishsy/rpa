package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1004", name = "元素属性类型")
public enum AttributeType {
    id("id"),
    _name("name"),
    xpath("xpath"),
    className("className"),
    tagName("tagName"),
    cssSelector("cssSelector"),
    linkText("linkText");

    String name;
    AttributeType(String name){
        this.name = name;
    }
}
