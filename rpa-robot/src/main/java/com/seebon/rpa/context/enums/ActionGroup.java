package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1011", name = "操作指令分组")
public enum ActionGroup {
    flow("流程控制"),
    web("Web操作"),
    net("网络请求"),
    data("数据处理"),
    excel("Excel操作"),
    word("Word操作"),
    tool("系统工具"),
    win("Win操作"),
    ai("人工智能AI"),
    custom("自定义"),
    offer("报盘"),
    declare("申报机器人");

    String name;
    ActionGroup(String name){
        this.name = name;
    }
}
