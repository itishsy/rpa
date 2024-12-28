package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1015", name = "列表操作方法")
public enum ListAction {
    remove("移除"),
    removeByCond("按条件移除"),
    order("排序"),
    orderFiled("根据字段值排序（数值）"),
    update("修改"),
    add("添加"),
    addAll("list合并"),
    addItem("添加字段"),
    get("获取"),
    group("分组"),
    getField("获取字段值"),
    distinct("去重"),
    filter("过滤"),
    merge("合并"),
    map("转map"),
    decode("解码"),
    addAllJoin("list拼接"),
    ;

    String name;
    ListAction(String name){
        this.name = name;
    }
}
