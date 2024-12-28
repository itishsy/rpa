package com.seebon.rpa.entity.agent.enums;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public enum RuleConstant {
    DATE_FORMAT_ONE("10006001", "yyyy-MM-dd"),
    DATE_FORMAT_TWO("10006002", "yyyyMMdd"),
    DATE_FORMAT_THREE("10006003", "yyyy/MM/dd"),
    DATE_FORMAT_FOUR("10006004", "yyyy-MM"),
    DATE_FORMAT_FIVE("10006005", "yyyy/MM"),
    DATE_FORMAT_SIX("10006006", "yyyyMM"),
    INSURANCES_ONE("10003001", "养老"),
    INSURANCES_TWO("10003002", "工伤"),
    INSURANCES_THREE("10003003", "生育"),
    INSURANCES_FOUR("10003004", "失业"),
    INSURANCES_FIVE("10003005", "重疾"),
    INSURANCES_SEVEN("10004001", "公积金"),
    INSURANCES_TYPE_YES_ONE("10005001", "往前补缴月数"),
    INSURANCES_TYPE_YES_TWO("10005002", "最早可补缴年月"),
    INSURANCES_TYPE_YES_THREE("10005003", "当前自然月"),
    ;

    private String code;
    private String value;

    RuleConstant(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValue(String exportFormat, String code) {
        for (RuleConstant rule : RuleConstant.values()) {
            if (rule.code.equals(code)) {
                return rule.value;
            }
        }
        return "";
    }

    //取整
    public static Integer getInteger(String code, Double num) {
        //四舍五入取整
        if ("10001001".equals(code)) {
            return (int) Math.rint(num);
        }
        return null;
    }

    //保留1位小数
    public static String getFormat(String code, String num) {
        String[] split = num.split("\\.");
        List<String> list = Lists.newArrayList(split);
        String result;
        String separator = null;
        String decimal = null;
        List<String> multiplication = null;
        switch (code) {
            case "10001001": {
                result = decimal("1", list);
                return result;
            }
            case "10001002": {
                //取一位小数点
                decimal = decimal("2", list);
                result = list.get(0) + "." + decimal;
                return result;
            }
            case "10001003": {
                //取两位小数点
                decimal = decimal("3", list);
                result = list.get(0) + "." + decimal;
                return result;
            }
            case "10001004": {
                //取两位小数点
                decimal = decimal("4", list);
                result = list.get(0) + "." + decimal;
                return result;
            }
            case "10001005":
                return getSeparator(list);
            case "10001006":
                separator = getSeparator(list);
                decimal = decimal("2", list);
                result = separator + "." + decimal;
                return result;
            case "10001007":
                separator = getSeparator(list);
                decimal = decimal("3", list);
                result = separator + "." + decimal;
                return result;
            case "10001008":
                separator = getSeparator(list);
                decimal = decimal("4", list);
                result = separator + "." + decimal;
                return result;
            case "10001009":
                return getSeparator(list) + "%";
            case "100010010":
                separator = getSeparator(list);
                decimal = decimal("2", list);
                result = separator + "." + decimal + "%";
                return result;
            case "100010011":
                separator = getSeparator(list);
                decimal = decimal("3", list);
                result = separator + "." + decimal + "%";
                return result;
            case "100010012":
                multiplication = centuplicate(num);
                result = decimal("1", multiplication);
                return result;
            case "100010013":
                multiplication = centuplicate(num);
                decimal = decimal("2", multiplication);
                result = multiplication.get(0) + "." + decimal;
                return result;
            case "100010014":
                multiplication = centuplicate(num);
                decimal = decimal("3", multiplication);
                result = multiplication.get(0) + "." + decimal;
                return result;
            case "100010015":
                multiplication = centuplicate(num);
                decimal = decimal("4", multiplication);
                result = multiplication.get(0) + "." + decimal;
                return result;
            default:
                return null;
        }
    }

    private static String decimal(String code, List<String> list) {
        String s1 = null;
        switch (code) {
            case "1":
                return list.get(0);
            case "2":
                //取一位小数点
                s1 = list.get(1).substring(0, 1);
                return s1;
            case "3":
                //取两位小数点
                if (list.get(1).length() >= 2) {
                    s1 = list.get(1).substring(0, 2);
                } else {
                    s1 = list.get(1).charAt(0) + "0";
                }
                return s1;
            case "4":
                //取两位小数点
                if (list.get(1).length() >= 2) {
                    s1 = list.get(1).substring(0, 3);
                } else {
                    s1 = list.get(1).charAt(0) + "00";
                }
                return s1;
            default:
                return null;
        }
    }

    private static String getSeparator(List<String> list) {
        String s3 = list.get(0);
        char[] nums = s3.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nums.length; i++) {
            String s4 = null;
            if (i % 3 == 0 && i + 1 != nums.length && i >= 3) {
                s4 = nums[i] + ",";
                sb.append(s4);
            } else if ((i + 1 == nums.length && i % 3 == 0) || i <= 3) {
                sb.append(nums[i]);
            }
        }
        return sb.toString();
    }

    private static List<String> centuplicate(String num) {
        BigDecimal bd = new BigDecimal(num);
        String s = bd.multiply(new BigDecimal(100)).toString();
        return Lists.newArrayList(s.split("\\."));
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
