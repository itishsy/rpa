package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1023", name = "字符串操作方法")
public enum StringMethod {
    split("切分"),
    contains("包含"),
    replace("替换"),
    infoExtraction("信息提取"),
    join("拼接"),
    encode("encode"),
    getPosition("获取字符串位置"),
    regReplace("正则表达式替换");

    String name;
    StringMethod(String name){
        this.name = name;
    }
}
