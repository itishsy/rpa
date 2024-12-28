package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-28 15:15:39
 */
public enum ColorEnum {

    White(1, "白色"),
    Blue(2, "蓝色"),
    Red(3, "红色"),
    Black(4, "黑色"),
    Yellow(5, "黄色"),
    Green(6, "绿色"),
    Cyan(7, "青色"),
    Magenta(8, "洋红色");

    private Integer code;

    private String name;

    ColorEnum(Integer code, String name) {
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
        ColorEnum[] values = ColorEnum.values();
        for (ColorEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        ColorEnum[] values = ColorEnum.values();
        for (ColorEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }
}
