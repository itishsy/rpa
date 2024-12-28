package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1031", name = "PDF操作方法")
public enum ActionType {
    getPayStartMonth("获取缴费起始年月"),
    getRegisterNumber("获取缴费人数"),
    readPdf("读取pdf表格"),
    readHaiKouRegistered("读取海口在册pdf表格"),
    readHangZhouRegistered("读取杭州在册pdf表格"),
    getTableCount("获取列表记录条数")
    ;

    String name;

    ActionType(String name) {
        this.name = name;
    }
}
