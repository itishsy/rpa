package com.seebon.rpa.entity.robot.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe 项目预警类型
 * @date 2023/4/14 16:03
 */
public enum WarnStatus{
    RPA_EXCEPTION("10017001", "RPA执行异常"),
    SITE_FAIL_EXCEPTION("10017002", "网站功能异常"),
    LOGIN_FAIL("10017003", "登录失败"),
    ROBOT_HEART_EXCEPTION("10017004", "机器人心跳异常"),
    ROBOT_UPGRADE_FAIL("10017005", "盒子升级失败"),
    CERT_WAIT_UPGRADE("10017006", "证书需升级"),
    API_EXCEPTION("10017007","接口服务异常");

    private String code;
    private String name;

    WarnStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code) {
        WarnStatus[] values = WarnStatus.values();
        for (WarnStatus item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static WarnStatus getEnumByCode(String code) {
        WarnStatus[] values = WarnStatus.values();
        for (WarnStatus item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }


}
