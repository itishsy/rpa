package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1978", name = "附件分组")
public enum EmployeeFileGroup {
    TRUE("是"),
    FALSE("否"),
    ;

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    EmployeeFileGroup(String name) {
        this.name = name;
    }
}
