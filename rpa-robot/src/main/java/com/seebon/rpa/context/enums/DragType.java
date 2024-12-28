package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-04 11:26:47
 */
@DataDictKey(value = "1034", name = "滑动类型")
public enum DragType {

    slideFill("滑块填充"),
    singleSlider("单滑块");

    String name;

    DragType(String name) {
        this.name = name;
    }

}
