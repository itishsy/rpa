package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;
import org.apache.commons.lang3.StringUtils;

@DataDictKey(value = "1958", name = "模板类型")
public enum TplTypeCode {
    allInsurance("社保系统", "10007001"),
    social("养老系统", "10007002"),
    medicare("医保系统", "10007003"),
    singleWorkInjury("单工伤", "10007004"),
    workInjury("工伤系统", "10007005"),
    filingSystem("备案系统", "10007006"),
    accfund("公积金", "10008001");

    String name;

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

    TplTypeCode(String name, String tplCode) {
        this.name = name;
        this.tplCode = tplCode;
    }

    public static TplTypeCode getTplTypeCode(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (TplTypeCode item : TplTypeCode.values()) {
            if (item.equals(TplTypeCode.valueOf(name))) {
                return item;
            }
        }
        return null;
    }
}
