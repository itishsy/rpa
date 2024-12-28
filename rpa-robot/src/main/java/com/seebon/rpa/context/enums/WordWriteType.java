package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-03-21 15:54:48
 */
@DataDictKey(value = "1050", name = "word修改方式")
public enum WordWriteType {

    replace("替换内容"),
    insertTable("插入表格");

    String name;
    WordWriteType(String name){
        this.name = name;
    }

}
