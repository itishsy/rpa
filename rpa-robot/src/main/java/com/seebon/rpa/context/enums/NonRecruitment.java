package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1982", name = "是否剔除非增员名单人员")
public enum NonRecruitment {

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

    NonRecruitment(String name) {
        this.name = name;
    }
}
