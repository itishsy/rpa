package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1007", name = "浏览器操作")
public enum BrowserAction {
    start("启动"),
//    close("关闭"),
    clearBrowserData("清理浏览器缓存"),
    quit("退出"),
    quitAll("退出所有"),
    switchTo("切换浏览器"),
    closeOtherTab("关闭自动打开标签页");

    String name;
    BrowserAction(String name){
        this.name = name;
    }
}
