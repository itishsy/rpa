package com.seebon.rpa.entity.saas.enums.payFee;

public enum PayFeeSystemTypeEnum {

    AllSocial("10007001", "社保全险系统", "10018001"),
    SocialRetirement("10007002", "养老系统", "10018002"),
    SocialMedicalInsurance("10007003", "医保系统", "10018003"),
    SingleWorkInjury("10007004", "单工伤系统", "10018004"),
    WorkInjury("10007005", "工伤系统", "10018005"),
    Accfund("10004001", "公积金", "10018006"),
    SuppAccfund("10004002", "补充公积金", "10018007");


    private String code;
    private String name;
    private String flowTagCode;

    PayFeeSystemTypeEnum(String code, String name, String flowTagCode) {
        this.code = code;
        this.name = name;
        this.flowTagCode = flowTagCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getFlowTagCode() {
        return flowTagCode;
    }

    public static String getNameByCode(String code) {
        PayFeeSystemTypeEnum[] values = PayFeeSystemTypeEnum.values();
        for (PayFeeSystemTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static String getFlowTagCodeByCode(String code) {
        PayFeeSystemTypeEnum[] values = PayFeeSystemTypeEnum.values();
        for (PayFeeSystemTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getFlowTagCode();
            }
        }
        return "";
    }

    public static PayFeeSystemTypeEnum getEnumByCode(String code) {
        PayFeeSystemTypeEnum[] values = PayFeeSystemTypeEnum.values();
        for (PayFeeSystemTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
