package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author zjf
 * @dateTime 2023-07-11 14:48:10
 */
public enum DeclareWebsiteEnum{

    ALL_RISK_SYSTEM("10007001", "全险系统"),
    RETIREMENT_SYSTEM("10007002", "养老系统"),
    MEDICAL_SYSTEM("10007003", "医疗系统"),
    SINGLE_WORK("10007004", "单工伤"),
    ACCFUND("10008001","公积金系统");


    private String code;
    private String name;

    DeclareWebsiteEnum(String code,String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code) {
        DeclareWebsiteEnum[] values = DeclareWebsiteEnum.values();
        for (DeclareWebsiteEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static DeclareWebsiteEnum getEnumByCode(Integer code) {
        DeclareWebsiteEnum[] values = DeclareWebsiteEnum.values();
        for (DeclareWebsiteEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
