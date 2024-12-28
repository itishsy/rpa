package com.seebon.rpa.entity.agent.enums;

import com.google.common.collect.Lists;
import com.seebon.rpa.utils.FormatUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-17 18:04:21
 */
public enum ContrastModeEnum {

    Equal(1, "等于", "==", ""),
    UnEqual(2, "不等于", "!=", ""),
    Contains(3, "包含", ".indexOf(", ")>=0"),
    UnContains(4, "不包含", ".indexOf(", ")<0"),
    GreaterThan(5, "大于", ">", ""),
    GreaterThanOrEqual(6, "大于等于", ">=", ""),
    LessThan(7, "小于", "<", ""),
    LessThanOrEqual(8, "小于等于", "<=", ""),
    IsNull(9, "为空", "==null", "==''"),
    IsNotNull(10, "不为空", "!=null", "!=''");


    private Integer code;
    private String text;
    private String rulePrefix;
    private String ruleSuffix;

    ContrastModeEnum(Integer code, String text, String rulePrefix, String ruleSuffix) {
        this.code = code;
        this.text = text;
        this.rulePrefix = rulePrefix;
        this.ruleSuffix = ruleSuffix;
    }

    public static String getTextByCode(Integer code) {
        ContrastModeEnum[] values = ContrastModeEnum.values();
        for (ContrastModeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getText();
            }
        }
        return "";
    }

    public static String getRulePrefixByCode(Integer code) {
        ContrastModeEnum[] values = ContrastModeEnum.values();
        for (ContrastModeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getRulePrefix();
            }
        }
        return "";
    }

    public static String getRuleSuffixByCode(Integer code) {
        ContrastModeEnum[] values = ContrastModeEnum.values();
        for (ContrastModeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getRuleSuffix();
            }
        }
        return "";
    }

    public static ContrastModeEnum getEnumByCode(Integer code) {
        ContrastModeEnum[] values = ContrastModeEnum.values();
        for (ContrastModeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getContrastStr(String val1, String val2, Integer code) {
        ContrastModeEnum enumByCode = getEnumByCode(code);
        if (code == 9) { // 为空
            return String.format("('%s'%s||'%s'%s)", val1, enumByCode.getRulePrefix(), val1, enumByCode.getRuleSuffix());
        } else if (code == 10) { // 不为空
            return String.format("('%s'%s&&'%s'%s)", val1, enumByCode.getRulePrefix(), val1, enumByCode.getRuleSuffix());
        }
        String format = "'%s'%s'%s'%s";
        if (Lists.newArrayList(1,2,5,6,7,8).contains(code)
                && isNumber(val1) && isNumber(val2)) {
            format = "%s%s%s%s";
            return String.format(format, val1, enumByCode.getRulePrefix(), val2, enumByCode.getRuleSuffix());
        } else if (Lists.newArrayList(1,2,5,6,7,8).contains(code)
                && FormatUtil.castDateByStr(val1)!=null && FormatUtil.castDateByStr(val2)!=null) {
            String v1 = new String(val1);
            String v2 = new String(val2);

            v1 = FormatUtil.repalceAllFormatter(v1);
            v2 = FormatUtil.repalceAllFormatter(v2);

            format = "%s%s%s%s";
            return String.format(format, v1, enumByCode.getRulePrefix(), v2, enumByCode.getRuleSuffix());
        } else {
            return String.format(format, val1, enumByCode.getRulePrefix(), val2, enumByCode.getRuleSuffix());
        }

    }

    private static boolean isNumber(String val) {
        if (StringUtils.isNotBlank(val)) {
            try {
                Double.parseDouble(val);
                return true;
            }catch (Exception e) {

            }
        }
        return false;
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
