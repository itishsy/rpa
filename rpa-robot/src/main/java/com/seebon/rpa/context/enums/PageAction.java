package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1006", name = "元素属性类型")
public enum PageAction {
    access("访问地址"),
    accessAndDo("访问地址并输入密码"),
    open("打开新页面"),
    close("关闭"),
    back("返回"),
    forward("前进"),
    switchTo("切换窗口"),
    refresh("刷新"),
    windowNumber("获取窗口数量"),
    closeOtherHandle("关闭其他窗口"),
    currentUrl("当前窗口链接"),
    ;

    String name;

    PageAction(String name) {
        this.name = name;
    }
}
