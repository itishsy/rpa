package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2021-04-09 13:49:14
 */
public enum EmDataSourceTable {

    Base("10000", "员工基础信息"),
    Declare("20000", "员工投保信息"),
    Account("30000", "员工账户信息"),
    Insurance("40000", "参保险种"),
    Other("50000", "其他"),
    Customize("60000", "自定义");
    private String code;
    private String name;

    EmDataSourceTable(String code, String name) {
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
