package com.seebon.rpa.entity.robot.enums;

public enum LoginAuthLoginStatusEnum {
    NO_LOGIN(0, "未维持"),
    LOGIN_MAINTAIN(1, "维持中"),
    INVALID(2, "已失效"),
    ;

    private Integer status;

    private String statusName;

    LoginAuthLoginStatusEnum(Integer status, String statusName) {
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
