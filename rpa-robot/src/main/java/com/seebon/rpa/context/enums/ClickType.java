package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1008", name = "点击方式")
public enum ClickType {
    click("单击"),
    doubleClick("双击"),
    mouseClick("鼠标点击"),
    jsClick("JS点击"),
    actionClick("Action点击"),
    rightClick("右击"),
    clickAndAlert("点击并关闭消息"),
    clickAndConfirm("点击并确认消息"),
    clickAndEnter("单击并回车"),
    mouseClickAndScreen("鼠标点击屏幕中间"),
    ;

    String name;
    ClickType(String name){
        this.name = name;
    }
}
