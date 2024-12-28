package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1062", name = "申报数据字段")
public enum DeclareDataField {
    idCard("身份证号（idCard）"),
    failReason("失败原因（failReason）"),
    declareStatus("申报状态（declareStatus）"),
    robotExecStatus("执行状态（robotExecStatus）"),
    extend("扩展字段（extend）");

    String name;
    DeclareDataField(String name){
        this.name = name;
    }
}
