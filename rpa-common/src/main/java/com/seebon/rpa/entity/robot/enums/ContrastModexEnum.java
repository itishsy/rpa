package com.seebon.rpa.entity.robot.enums;

public enum ContrastModexEnum {
    Equal(1, "等于", ".equals(", ")"),
    UnEqual(2, "不等于", ".equals(", ")"),
    Contains(3, "包含", ".contains(", ")"),
    UnContains(4, "不包含", ".contains(", ")"),
    ;
    private Integer code;
    private String text;
    private String rulePrefix;
    private String ruleSuffix;

    ContrastModexEnum(Integer code, String text, String rulePrefix, String ruleSuffix) {
        this.code = code;
        this.text = text;
        this.rulePrefix = rulePrefix;
        this.ruleSuffix = ruleSuffix;
    }

    public static ContrastModexEnum getEnumByCode(Integer code) {
        ContrastModexEnum[] values = ContrastModexEnum.values();
        for (ContrastModexEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getTextByCode(Integer code) {
        for (ContrastModexEnum e : ContrastModexEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getText();
            }
        }
        return null;
    }

    public static String getContrastStr(String val1, String val2, Integer code) {
        ContrastModexEnum enumByCode = getEnumByCode(code);
        if (code == 1) {
            return String.format("%s%s%s%s", val1, enumByCode.getRulePrefix(), val2, enumByCode.getRuleSuffix());
        } else if (code == 2) {
            return String.format("!%s%s%s%s", val1, enumByCode.getRulePrefix(), val2, enumByCode.getRuleSuffix());
        } else if (code == 3) {
            return String.format("%s%s%s%s", val2, enumByCode.getRulePrefix(), val1, enumByCode.getRuleSuffix());
        } else if (code == 4) {
            return String.format("!%s%s%s%s", val2, enumByCode.getRulePrefix(), val1, enumByCode.getRuleSuffix());
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public String getRulePrefix() {
        return rulePrefix;
    }

    public String getRuleSuffix() {
        return ruleSuffix;
    }
}
