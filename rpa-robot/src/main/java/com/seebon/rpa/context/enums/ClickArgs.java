package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1026", name = "点击参数")
public enum ClickArgs {
    notNullClick("非空点击"),
    clear("清空内容"),
    notSwitchPage("不切换窗口"),
    downloadFile("文件下载"),
    closeOtherHandle("关闭其他窗口");

    String name;

    ClickArgs(String name){
        this.name = name;
    }
}
