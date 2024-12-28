package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-10 10:03:58
 */
@DataDictKey(value = "1040", name = "分组类型")
public enum GroupBy {

    byValue("根据值分组"),
    bySize("分批");

    String name;

    GroupBy(String name) {
        this.name = name;
    }
}
