package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1056", name = "页面操作类型")
public enum PageOperationType {
    //handle("需要窗口维持"),
    downloadFile("下载文件");

    String name;

    PageOperationType(String name) {
        this.name = name;
    }
}
