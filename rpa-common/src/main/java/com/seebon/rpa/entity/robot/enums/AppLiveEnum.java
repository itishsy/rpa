package com.seebon.rpa.entity.robot.enums;

public enum AppLiveEnum{
    live(1, "上线"),
    offline(0, "下线"),
    launched(2, "待上线"),
    ;

    private Integer status;

    private String statusName;

    AppLiveEnum(Integer status,String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public static String getNameByCode(Integer status) {
        if (status != null) {
            AppLiveEnum[] values = AppLiveEnum.values();
            for (AppLiveEnum item : values) {
                if (item.getStatus().equals(status)) {
                    return item.getStatusName();
                }
            }
        }
        return "";
    }

    public static AppLiveEnum getEnumByCode(Integer status) {
        if (status != null) {
            AppLiveEnum[] values = AppLiveEnum.values();
            for (AppLiveEnum item : values) {
                if (item.getStatus().equals(status)) {
                    return item;
                }
            }
        }
        return null;
    }
}
