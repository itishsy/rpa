package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-04 18:18:32
 */
@DataDictKey(value = "1035", name = "滑动类型")
public enum SelectByType {
    byText("根据文本选择"),
    byValue("根据值选择"),
    byIndex("根据序号选择"),
    byULi("根据Li元素匹配"),
    byClick("根据点击元素匹配"),
    ;

    String name;

    SelectByType(String name) {
        this.name = name;
    }
}
