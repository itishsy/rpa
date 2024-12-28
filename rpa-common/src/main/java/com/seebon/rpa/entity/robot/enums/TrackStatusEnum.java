package com.seebon.rpa.entity.robot.enums;

import lombok.Getter;

/**
 * 条件字段
 * <p>
 * (1-应用名称、2-申报账户、3-申报系统、4-事项、5-登录方式)
 */
@Getter
public enum TrackStatusEnum {
    queue("1", "排队中"),
    offline("2", "离线"),
    pause("3", "暂停"),
    run("4", "执行中"),
    check("5", "审核中"),
    completed("6", "已完成"),
    interrupt("7", "执行中断"),
    unReached("8", "未到申报期"),
    ;

    private String code;
    private String name;

    TrackStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        for (TrackStatusEnum e : TrackStatusEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return null;
    }
}
