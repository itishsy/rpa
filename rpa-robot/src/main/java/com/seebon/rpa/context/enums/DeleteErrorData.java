package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1961", name = "删除数据类型")
public enum DeleteErrorData {

    excel("报盘文件"),
    declareList("缓存数据"),
    both("报盘文件和缓存数据"),
    custom("自定义文件"),
    qfPath("启封文件"),
    zrPath("转入文件"),
    ;

    String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DeleteErrorData(String name) {
        this.name = name;
    }
}
