package com.seebon.rpa.utils.enums;

public enum TplTypeCodeEnum {
    qx("10007001", "社保系统"),
    yangLao("10007002", "养老系统"),
    yiLiao("10007003", "医疗系统"),
    dgs("10007004", "单工伤"),
    gongShang("10007005", "工伤系统"),
    beiAn("10007006", "备案系统"),
    shuiWu("10007007", "税务系统"),
    jinbao("10007008", "金保系统"),
    shiye("10007009", "失业系统"),
    gjj("10008001", "公积金系统"),
    gggjj("10008002", "国管公积金系统");

    private String code;
    private String name;

    TplTypeCodeEnum(String code, String name) {
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
        TplTypeCodeEnum[] values = TplTypeCodeEnum.values();
        for (TplTypeCodeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static String getCodeByName(String name) {
        TplTypeCodeEnum[] values = TplTypeCodeEnum.values();
        for (TplTypeCodeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static TplTypeCodeEnum getEnumByCode(String code) {
        TplTypeCodeEnum[] values = TplTypeCodeEnum.values();
        for (TplTypeCodeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
