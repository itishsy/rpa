package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-13 09:48:10
 */
public enum BusinessTypeEnum {

    Social(1, "1001001", "社保"),
    Accfund(2, "1001002", "公积金");

    private Integer code;
    private String key;
    private String name;

    BusinessTypeEnum(Integer code, String key, String name) {
        this.code = code;
        this.key = key;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public static String getNameByCode(Integer code) {
        BusinessTypeEnum[] values = BusinessTypeEnum.values();
        for (BusinessTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        BusinessTypeEnum[] values = BusinessTypeEnum.values();
        for (BusinessTypeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static BusinessTypeEnum getEnumByCode(Integer code) {
        BusinessTypeEnum[] values = BusinessTypeEnum.values();
        for (BusinessTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getNameByKey(String key) {
        BusinessTypeEnum[] values = BusinessTypeEnum.values();
        for (BusinessTypeEnum item : values) {
            if (item.getKey().equals(key)) {
                return item.getName();
            }
        }
        return null;
    }

    public static Integer getCodeByKey(String key) {
        BusinessTypeEnum[] values = BusinessTypeEnum.values();
        for (BusinessTypeEnum item : values) {
            if (item.getKey().equals(key)) {
                return item.getCode();
            }
        }
        return null;
    }
}
