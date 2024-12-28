package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-17 17:53:26
 */
public enum RequiredEnum {

    BT(2, "必填"),
    FBT(1, "非必填");

    private Integer code;
    private String text;

    RequiredEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return text;
    }

}
