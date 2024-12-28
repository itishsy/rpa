package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-17 18:13:38
 */
public enum RelationEnum {
    AND(1, "并且", "&&"),
    OR(2, "或者", "||");


    private Integer code;

    private String name;

    private String symbol;

    RelationEnum(Integer code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public static String getTextByCode(Integer code) {
        RelationEnum[] values = RelationEnum.values();
        for (RelationEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static String getSymbolByCode(Integer code) {
        RelationEnum[] values = RelationEnum.values();
        for (RelationEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getSymbol();
            }
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
