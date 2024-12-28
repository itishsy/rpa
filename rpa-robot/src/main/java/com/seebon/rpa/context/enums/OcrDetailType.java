package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1055", name = "应用点击方式")
public enum OcrDetailType {

    onlyText("仅文本"),
    detailInfo("详细信息");

    String name;

    OcrDetailType(String name){
        this.name = name;
    }

}
