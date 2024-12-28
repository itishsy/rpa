package com.seebon.rpa.entity.robot.enums;

public enum OnLiveEnum {
    live(1, "上线"),
    offline(2, "下线"),
    launched(3, "待上线"),
    ;

    private Integer status;

    private String statusName;

    OnLiveEnum(Integer status, String statusName) {
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
            OnLiveEnum[] values = OnLiveEnum.values();
            for (OnLiveEnum item : values) {
                if (item.getStatus().equals(status)) {
                    return item.getStatusName();
                }
            }
        }
        return "";
    }

    public static OnLiveEnum getEnumByCode(Integer status) {
        if (status != null) {
            OnLiveEnum[] values = OnLiveEnum.values();
            for (OnLiveEnum item : values) {
                if (item.getStatus().equals(status)) {
                    return item;
                }
            }
        }
        return null;
    }
}
