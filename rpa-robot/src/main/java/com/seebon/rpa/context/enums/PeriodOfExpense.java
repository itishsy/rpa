package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1972", name = "费用所属期")
public enum PeriodOfExpense {
    lastMonth("上月"),
    currentMonth("当月"),
    nextMonth("下月"),
    ;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    PeriodOfExpense(String name) {
        this.name = name;
    }
}
