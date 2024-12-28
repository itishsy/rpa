package com.seebon.rpa.entity.auth.enums;

public enum UserTypeEnum {

    AdminUser(1, "运营用户"),
    ClientUser(2, "客户用户");
    
    private Integer code;
    
    private String name;

    UserTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        UserTypeEnum[] values = UserTypeEnum.values();
        for (UserTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static UserTypeEnum getEnumByCode(Integer code) {
        UserTypeEnum[] values = UserTypeEnum.values();
        for (UserTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
