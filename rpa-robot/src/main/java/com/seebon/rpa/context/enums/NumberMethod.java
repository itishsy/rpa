package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1030", name = "数字处理方法")
public enum NumberMethod {
    sumList("列表求和"),
    calculation("计算");

    String name;

    NumberMethod(String name) {
        this.name = name;
    }
}
