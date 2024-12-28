package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author LY
 * @date 2023/10/26 16:47
 */

@DataDictKey(value = "1456", name = "操作类型")
public enum OfferLabelSocialAddType {

    newInsured("1001","新参开户",null,1),
    changeInto("1003","转移转入",null,1),
    renewal("1004","续保",1,1),
    singleInsurancePadded("1005","单险种补齐",1,1),
    changeBaseAndRatio("1012","调基调比",null,1);


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

    OfferLabelSocialAddType(String code, String name, Integer businessType, Integer changeType){
        this.code = code;
        this.name = name;
        this.businessType = businessType;
        this.changeType = changeType;
    }

    public static String getCode(String name) {
        for (OfferLabelSocialAddType item : OfferLabelSocialAddType.values()) {
            if (item.equals(OfferLabelSocialAddType.valueOf(name))) {
                return item.getCode();
            }
        }
        return null;
    }
}
