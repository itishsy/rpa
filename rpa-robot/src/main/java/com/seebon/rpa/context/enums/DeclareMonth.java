package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1957", name = "申报年月")
public enum DeclareMonth {
    lastMonth("上月"),
    currentMonth("当月"),
    nextMonth("下月"),
    excelColumn("指定Excel列"),
    customMonth("自定义");
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DeclareMonth(String name) {
        this.name = name;
    }
}
