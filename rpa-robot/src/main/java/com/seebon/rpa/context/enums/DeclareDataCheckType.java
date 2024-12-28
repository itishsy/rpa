package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1986", name = "申报数据检查类型")
public enum DeclareDataCheckType {
    unTagDeclareData("未标记待申报的数据"),
    unDoneDeclareData("未完成申报操作的数据"),
//    unReturnDeclareResult("已操作申报未核验的数据")
    ;

    String name;

    DeclareDataCheckType(String name) {
        this.name = name;
    }
}
