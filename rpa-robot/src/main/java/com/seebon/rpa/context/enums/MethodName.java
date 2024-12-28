package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-02-03 16:18:36
 */
@DataDictKey(value = "1042", name = "实体操作方法")
public enum MethodName {
    putAll("putAll"),
    clear("clear"),
    contains("contains");

    String name;
    MethodName(String name){
        this.name = name;
    }

}
