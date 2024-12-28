package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1029", name = "执行JS语句类型")
public enum ExeJSType {
    removeAttr("删除属性"),
    updateStyle("修改样式"),
    downloadFile("下载文件");

    String name;

    ExeJSType(String name) {
        this.name = name;
    }
}
