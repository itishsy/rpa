package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1049", name = "身份证处理方法")
public enum IdCardMethod {
    birthday("出生日期"),
    sex("性别");

    String name;
    IdCardMethod(String name){
        this.name = name;
    }
}
