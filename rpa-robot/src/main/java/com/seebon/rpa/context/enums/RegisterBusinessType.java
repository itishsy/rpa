package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author LY
 * @date 2023/10/26 16:24
 */

@DataDictKey(value = "1118", name = "业务类型")
public enum RegisterBusinessType {

    allInsurance("全险", 1,"10007001"),
    social("社保", 1,"10007002"),
    medicare("医保", 1,"10007003"),
    accfund("公积金", 2,"10008001");

    String name;

    Integer type;

    String tplCode;

    public String getTplCode() {
        return tplCode;
    }

    public void setTplCode(String tplCode) {
        this.tplCode = tplCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    RegisterBusinessType(String name, Integer type, String tplCode) {
        this.name = name;
        this.type = type;
        this.tplCode = tplCode;
    }
}
