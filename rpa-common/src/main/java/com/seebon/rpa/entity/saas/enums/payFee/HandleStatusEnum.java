package com.seebon.rpa.entity.saas.enums.payFee;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-12 14:40
 */
public enum  HandleStatusEnum{

    AUDIT(2, "待审核"),
    UNCHECK(3, "审核不通过"),
    UNRECEIVED(1, "未到账"),
    RECEIVED(0, "已到账");

    private Integer code;
    private String name;

    HandleStatusEnum(Integer code, String name) {
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
        HandleStatusEnum[] values = HandleStatusEnum.values();
        for (HandleStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static HandleStatusEnum getEnumByCode(Integer code) {
        HandleStatusEnum[] values = HandleStatusEnum.values();
        for (HandleStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
