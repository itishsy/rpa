package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-06 14:07:04
 */
@DataDictKey(value = "1036", name = "读取类型")
public enum ReadType {

    table("表格"),
    divTable("div表格"),
    text("文本"),
    ul("ul下拉选项"),
    select("下拉选项");

    String name;
    ReadType(String name) {
        this.name = name;
    }

}
