package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2023-02-22 19:50:52
 */
public enum FailTypeEnum {

    All(1, "全部失败"),
    Part(2, "部分失败");

    private Integer code;

    private String name;


    FailTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code) {
        FailTypeEnum[] values = FailTypeEnum.values();
        for (FailTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        FailTypeEnum[] values = FailTypeEnum.values();
        for (FailTypeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static FailTypeEnum getEnumByCode(String code) {
        FailTypeEnum[] values = FailTypeEnum.values();
        for (FailTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
