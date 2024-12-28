package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;
import org.apache.commons.lang3.StringUtils;

/**
 * @author LY
 * @date 2023/10/26 16:47
 */

@DataDictKey(value = "1100", name = "操作类型")
public enum OfferLabelType {
    newInsured("1001", "新参开户", null, 1),
    accountUnsealed("1002", "账户启封", 2, 1),
    changeInto("1003", "转移转入", null, 1),
    renewal("1004", "续保", 1, 1),
    singleInsurancePadded("1005", "单险种补齐", 1, 1),
    IncreaseFiling("1006", "增员备案", 1, 1),
    undoIncrease("1007", "撤销增员", 2, 2),
    accountSealed("1008", "减员封存", null, 2),
    accountRollOut("1009", "转出", null, 2),
    subtractFiling("1010", "减员备案", 1, 2),
    undoSubtract("1011", "撤销减员", 2, 1),
    changeBaseAndRatio("1012", "调基调比", null, 1),
    changeBase("1013", "调基", null, 3),
    supplementing("1014", "补缴", null, 5),
    check("1015", "待网站审核", null, 5),
    salary("1016", "工资申报", null, 5),
    loseJob("1017", "申报失业", null, 5),
    loseMedical("1018", "申报医保", null, 5),
    loseInjury("1019", "申报工伤", null, 5),
    appRenewal("1020", "员工app续保", null, 5),
    ;

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

    OfferLabelType(String code, String name, Integer businessType, Integer changeType) {
        this.code = code;
        this.name = name;
        this.businessType = businessType;
        this.changeType = changeType;
    }

    public static String getCode(String name) {
        for (OfferLabelType item : OfferLabelType.values()) {
            if (item.equals(OfferLabelType.valueOf(name))) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getName(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (OfferLabelType item : OfferLabelType.values()) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return null;
    }

    public static OfferLabelType getOfferLabelType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (OfferLabelType item : OfferLabelType.values()) {
            if (item.equals(OfferLabelType.valueOf(name))) {
                return item;
            }
        }
        return null;
    }
}
