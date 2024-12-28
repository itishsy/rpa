package com.seebon.rpa.entity.robot.enums;

public enum LoginAuthProcessStatusEnum {
    NO_LOGIN(0, "未登录"),
    IS_LOGIN(1, "已登录"),

    ;

    private Integer status;

    private String statusName;

    LoginAuthProcessStatusEnum(Integer status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }
}
