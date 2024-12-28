package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-24 15:49:49
 */
public enum TplTypeSaasEnum {


    AllInsurance("10007001", "社保系统", "社保"),
    PensionInsurance("10007002", "养老系统", "社保"),
    MedicalInsurance("10007003", "医疗系统", "医保"),
    SingleWorkRelatedInsurance("10007004", "单工伤", "社保"),
    WorkRelatedInsurance("10007005", "工伤系统", "社保"),
    Filings("10007006", "备案系统", "社保"),
    TaxSystem("10007007", "税务系统", "社保"),
    KingInsurance("10007008", "金保系统", "社保"),
    UnemploymentInsurance("10007009", "失业系统", "社保"),
    CityNetSystem("10007010", "市网系统", "社保"),
    AccfoundInsurance("10008001", "公积金系统", "公积金"),
    GGAccfoundInsurance("10008002", "国管公积金系统", "公积金");

    private String code;

    private String name;

    private String name1;

    TplTypeSaasEnum(String code, String name, String name1) {
        this.code = code;
        this.name = name;
        this.name1 = name1;
    }

    public static String getNameByCode(String code) {
        TplTypeSaasEnum[] values = TplTypeSaasEnum.values();
        for (TplTypeSaasEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static String getName1ByCode(String code) {
        TplTypeSaasEnum[] values = TplTypeSaasEnum.values();
        for (TplTypeSaasEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName1();
            }
        }
        return "";
    }

    public static TplTypeSaasEnum getEnumByCode(String code) {
        TplTypeSaasEnum[] values = TplTypeSaasEnum.values();
        for (TplTypeSaasEnum item : values) {
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

    public String getName1() {
        return name1;
    }
}
