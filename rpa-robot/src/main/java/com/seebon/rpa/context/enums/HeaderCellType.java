package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-06 14:23:24
 */
@DataDictKey(value = "1037", name = "表格头部元素类型")
public enum HeaderCellType {

    td("td"),
    th("th");

    String name;
    HeaderCellType(String name) {
        this.name = name;
    }

}
