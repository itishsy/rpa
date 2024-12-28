package com.seebon.rpa.entity.agent.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-24 15:49:49
 */
public enum TplTypeEnum {


    AllInsurance("10007001", "社保系统"),
    PensionInsurance("10007002", "养老系统"),
    MedicalInsurance("10007003", "医疗系统"),
    SingleWorkRelatedInsurance("10007004", "单工伤"),
    WorkRelatedInsurance("10007005", "工伤系统"),
    Filings("10007006", "备案系统"),
    TaxSystem("10007007", "税务系统"),
    KingInsurance("10007008", "金保系统"),
    UnemploymentInsurance("10007009", "失业系统"),
    CityNetSystem("10007010", "市网系统"),
    AccfoundInsurance("10008001", "公积金系统"),
    GGAccfoundInsurance("10008002", "国管公积金系统");

    private String code;

    private String name;

    TplTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return "";
        }
        TplTypeEnum[] values = TplTypeEnum.values();
        for (TplTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static TplTypeEnum getEnumByCode(String code) {
        TplTypeEnum[] values = TplTypeEnum.values();
        for (TplTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
