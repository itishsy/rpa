package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1027", name = "操作对象类型")
public enum TargetName {
    driverTarget("浏览器"),
    elementTarget("网页元素"),
    sheetTarget("Excel表格"),
    fileTarget("文件"),
    responseTarget("HTTP响应"),
    jdbcTarget("数据源"),
    objectTarget("自定义对象");

    String name;
    TargetName(String name){
        this.name = name;
    }
}
