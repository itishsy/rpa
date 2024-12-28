package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/11/7  15:32
 */
public enum ActionCodeEnum {
    VARIABLE("变量操作", "variable");

    private String name;

    private String code;

    ActionCodeEnum(String name, String code) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String name) {
        ActionCodeEnum[] values = ActionCodeEnum.values();
        for (ActionCodeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
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
