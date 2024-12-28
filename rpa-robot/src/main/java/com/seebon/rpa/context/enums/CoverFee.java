package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1983", name = "覆盖费用明细")
public enum CoverFee {
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

    CoverFee(String name) {
        this.name = name;
    }
}
