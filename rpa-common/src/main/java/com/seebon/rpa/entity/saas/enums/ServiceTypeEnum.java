package com.seebon.rpa.entity.saas.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-17 15:35
 */
public enum ServiceTypeEnum {
    ZH_SERVICE(0, "在户服务"),
    ZC_SERVICE(1, "驻场服务"),
    WQ_SERVICE(2, "外勤服务"),
    QT_SERVICE(3, "其他服务");

    private Integer code;
    private String name;

    ServiceTypeEnum(Integer code, String name) {
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
        ServiceTypeEnum[] values = ServiceTypeEnum.values();
        for (ServiceTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static ServiceTypeEnum getEnumByCode(Integer code) {
        ServiceTypeEnum[] values = ServiceTypeEnum.values();
        for (ServiceTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
