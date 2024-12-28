package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-18 11:22:21
 */
public enum DataCreateTypeEnum {

    PageInput("1", "页面录入"),
    PageImport("2", "批量导入"),
    WebService("3", "接口录入"),
    AliDingDing("4", "钉钉接入");

    private String code;

    private String name;

    DataCreateTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
