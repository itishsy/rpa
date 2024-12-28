package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1022", name = "流程变量操作方法")
public enum VariableMethod {
    add("添加"),
    append("拼接"),
    put("putMap"),
    remove("删除"),
    contains("存在"),
    httpSetVal("响应赋值");

    String name;
    VariableMethod(String name){
        this.name = name;
    }
}
