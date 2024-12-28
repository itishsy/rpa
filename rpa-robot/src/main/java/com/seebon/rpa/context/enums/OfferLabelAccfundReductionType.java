package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author LY
 * @date 2023/10/26 16:47
 */

@DataDictKey(value = "1657", name = "操作类型")
public enum OfferLabelAccfundReductionType {


    undoIncrease("1007", "撤销增员", 2, 2),
    accountSealed("1008", "减员封存", null, 2),
    accountRollOut("1009", "转出", null, 2),
    subtractFiling("1010", "减员备案", 1, 2);

    String code;
    String name;
    Integer businessType;
    Integer changeType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    OfferLabelAccfundReductionType(String code, String name, Integer businessType, Integer changeType) {
        this.code = code;
        this.name = name;
        this.businessType = businessType;
        this.changeType = changeType;
    }

    public static String getCode(String name) {
        for (OfferLabelAccfundReductionType item : OfferLabelAccfundReductionType.values()) {
            if (item.equals(OfferLabelAccfundReductionType.valueOf(name))) {
                return item.getCode();
            }
        }
        return null;
    }
}
