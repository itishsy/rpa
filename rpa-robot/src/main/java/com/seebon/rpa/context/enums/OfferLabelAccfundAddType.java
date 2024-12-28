package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author LY
 * @date 2023/10/26 16:47
 */

@DataDictKey(value = "1465", name = "操作类型")
public enum OfferLabelAccfundAddType {

    newInsured("1001", "新参开户", null, 1),
    accountUnsealed("1002", "账户启封", 2, 1),
    changeInto("1003", "转移转入", null, 1),

    undoSubtract("1011", "撤销减员", 2, 1),
    changeBaseAndRatio("1012", "调基调比", null, 1);

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

    OfferLabelAccfundAddType(String code, String name, Integer businessType, Integer changeType) {
        this.code = code;
        this.name = name;
        this.businessType = businessType;
        this.changeType = changeType;
    }

    public static String getCode(String name) {
        for (OfferLabelAccfundAddType item : OfferLabelAccfundAddType.values()) {
            if (item.equals(OfferLabelAccfundAddType.valueOf(name))) {
                return item.getCode();
            }
        }
        return null;
    }
}
