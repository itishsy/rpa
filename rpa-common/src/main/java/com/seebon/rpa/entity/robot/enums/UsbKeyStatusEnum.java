package com.seebon.rpa.entity.robot.enums;

/**
 * 状态:1-已插入未初始化/2-可用/3-已被挂载/4-重复KEY不可用/5-未插入
 */
public enum UsbKeyStatusEnum {
    unInit(1, "已插入未初始化"),
    available(2, "可用"),
    mounted(3, "已被挂载"),
    repeatUnAvailable(4, "重复KEY不可用"),
    unInsert(5, "未插入");

    private String name;

    private Integer code;

    UsbKeyStatusEnum(Integer code, String name) {
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
        UsbKeyStatusEnum[] values = UsbKeyStatusEnum.values();
        for (UsbKeyStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static UsbKeyStatusEnum getEnumByCode(Integer code) {
        UsbKeyStatusEnum[] values = UsbKeyStatusEnum.values();
        for (UsbKeyStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
