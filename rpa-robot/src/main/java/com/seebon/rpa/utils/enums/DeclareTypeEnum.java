package com.seebon.rpa.utils.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-09-27 19:03:15
 */
public enum DeclareTypeEnum {

    ZY(1, "增员"),
    JY(2, "减员"),
    TJ(3, "调基"),
    BJ(5, "补缴");

    private Integer code;
    private String name;

    DeclareTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(Integer code) {
        DeclareTypeEnum[] values = DeclareTypeEnum.values();
        for (DeclareTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        DeclareTypeEnum[] values = DeclareTypeEnum.values();
        for (DeclareTypeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static DeclareTypeEnum getEnumByCode(Integer code) {
        DeclareTypeEnum[] values = DeclareTypeEnum.values();
        for (DeclareTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
