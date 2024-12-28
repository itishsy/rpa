package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;
import org.apache.commons.lang3.StringUtils;

/**
 * @author LY
 * @date 2023/10/26 16:24
 */

@DataDictKey(value = "1099", name = "业务类型")
public enum BusinessType {

    allInsurance("社保系统", 1, "10007001"),
    social("养老系统", 1, "10007002"),
    medicare("医保系统", 1, "10007003"),
    singleWorkInjury("单工伤", 1, "10007004"),
    workInjury("工伤系统", 1, "10007005"),
    filingSystem("备案系统", 1, "10007006"),

    accfund("公积金", 2, "10008001");

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

    BusinessType(String name, Integer type, String tplCode) {
        this.name = name;
        this.type = type;
        this.tplCode = tplCode;
    }

    public static BusinessType getBusinessType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (BusinessType item : BusinessType.values()) {
            if (item.equals(BusinessType.valueOf(name))) {
                return item;
            }
        }
        return null;
    }
}
