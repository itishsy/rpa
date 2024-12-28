package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1977", name = "拖动方式")
public enum DragWay {

    move("移动"),
    dragAndDrop("拖放");

    String name;

    DragWay(String name) {
        this.name = name;
    }

}
