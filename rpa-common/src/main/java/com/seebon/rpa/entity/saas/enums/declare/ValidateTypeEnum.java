package com.seebon.rpa.entity.saas.enums.declare;

public enum ValidateTypeEnum {

    SMS("10021001", "短信"),
    QRCode("10021002", "二维码登录"),
    FaceQRCode("10021003", "人脸二维码");

    private String code;

    private String name;

    ValidateTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        ValidateTypeEnum[] values = ValidateTypeEnum.values();
        for (ValidateTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static ValidateTypeEnum getEnumByCode(String code) {
        ValidateTypeEnum[] values = ValidateTypeEnum.values();
        for (ValidateTypeEnum item : values) {
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
