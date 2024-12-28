package com.seebon.rpa.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-14 15:19:41
 */
public class FormatUtil {

    /**
     * 验证字符串的数据格式
     * @param formatCode 格式编码
     * 10001001：取整
     * 10001002：1位小数
     * 10001003：2位小数
     * 10001004：3位小数
     * 10001005：带分隔符取整
     * 10001006：带分隔符1位小数
     * 10001007：带分隔符2位小数
     * 10001008：带分隔符3位小数
     * 10001009：百分比取整
     * 10001010：百分比1位小数
     * 10001011：百分比2位小数
     * 10001012：乘百取整
     * 10001013：乘百1位小数
     * 10001014：乘百2位小数
     * 10001015：乘百3位小数
     * 10006001：yyyy-MM-dd
     * 10006002：yyyyMMdd
     * 10006003：yyyy/MM/dd
     * 10006004：yyyy-MM
     * 10006005：yyyy/MM
     * 10006006：yyyyMM
     * @param value 验证的字符串
     * @return
     */
    public static Boolean validateDataFormat(String formatCode, String value) {
        if (StringUtils.isBlank(formatCode) || StringUtils.isBlank(value))
            return false;
        value = value.trim();
        try {
            switch (formatCode) {
                case "10001001": { //取整
                    return StringUtils.isNumeric(value);
                }
                case "10001002": { //1位小数
                    return value.matches("[0-9]+(\\.[0-9]{1})");
                }
                case "10001003": { //2位小数
                    return value.matches("[0-9]+(\\.[0-9]{2})");
                }
                case "10001004": { //3位小数
                    return value.matches("[0-9]+(\\.[0-9]{3})");
                }
                case "10001005": { //带千位符取整
                    return isNumeric(value, null, ",###", ",");
                }
                case "10001006": { //带千位符1位小数
                    return isNumeric(value, "[0-9]+(\\.[0-9]{1})", ",###.0", ",");
                }
                case "10001007": { //带千位符2位小数
                    return isNumeric(value, "[0-9]+(\\.[0-9]{2})", ",###.00", ",");
                }
                case "10001008": { //带千位符3位小数
                    return isNumeric(value, "[0-9]+(\\.[0-9]{3})", ",###.000", ",");
                }
                case "10001009": { //百分比取整
                    return isNumeric(value, null, "#%", "%");
                }
                case "10001010": { //百分比1位小数
                    return isNumeric(value, "[0-9]+(\\.[0-9]{1})", "#.#%", "%");
                }
                case "10001011": { //百分比2位小数
                    return isNumeric(value, "[0-9]+(\\.[0-9]{2})", "#.##%", "%");
                }
                case "10001012": { //乘百取整
                    return StringUtils.isNumeric(value);
                }
                case "10001013": { //乘百1位小数
                    return value.matches("[0-9]+(\\.[0-9]{1})");
                }
                case "10001014": { //乘百2位小数
                    return value.matches("[0-9]+(\\.[0-9]{2})");
                }
                case "10001015": { //乘百3位小数
                    return value.matches("[0-9]+(\\.[0-9]{3})");
                }
                case "10006001": { //yyyy-MM-dd
                    return isDate(value, "yyyy-MM-dd");
                }
                case "10006002": { //yyyyMMdd
                    return isDate(value, "yyyyMMdd");
                }
                case "10006003": { //yyyy/MM/dd
                    return isDate(value, "yyyy/MM/dd");
                }
                case "10006004": { //yyyy-MM
                    return isDate(value, "yyyy-MM");
                }
                case "10006005": { //yyyy/MM
                    return isDate(value, "yyyy/MM");
                }
                case "10006006": { //yyyyMM
                    return isDate(value, "yyyyMM");
                }
                default:
                    return false;
            }
        }catch (Exception e) {
            return false;
        }
    }


    /**
     * 验证字符串的数据格式
     *
     * @param formatCode 格式编码
     *                   10001001：取整
     *                   10001002：1位小数
     *                   10001003：2位小数
     *                   10001004：3位小数
     *                   10001005：带分隔符取整
     *                   10001006：带分隔符1位小数
     *                   10001007：带分隔符2位小数
     *                   10001008：带分隔符3位小数
     *                   10001009：百分比取整
     *                   10001010：百分比1位小数
     *                   10001011：百分比2位小数
     *                   10001012：乘百取整
     *                   10001013：乘百1位小数
     *                   10001014：乘百2位小数
     *                   10001015：乘百3位小数
     *                   10006001：yyyy-MM-dd
     *                   10006002：yyyyMMdd
     *                   10006003：yyyy/MM/dd
     *                   10006004：yyyy-MM
     *                   10006005：yyyy/MM
     *                   10006006：yyyyMM
     * @param value      验证的字符串
     * @return String
     */
    public static String getValueByFormat(String formatCode, String value) {
        if (StringUtils.isBlank(formatCode) || StringUtils.isBlank(value)) {
            return "";
        }
        value = value.trim();
        try {
            switch (formatCode) {
                case "10001001": { //取整
                    return new DecimalFormat("#").format(new BigDecimal(value).setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                case "10001002": { //1位小数
                    return new DecimalFormat("0.0").format(new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP));
                }
                case "10001003": { //2位小数
                    return new DecimalFormat("0.00").format(new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                case "10001004": { //3位小数
                    return new DecimalFormat("0.000").format(new BigDecimal(value).setScale(3, BigDecimal.ROUND_HALF_UP));
                }
                case "10001005": { //带千位符取整
                    return new DecimalFormat(",###").format(new BigDecimal(value).setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                case "10001006": { //带千位符1位小数
                    return new DecimalFormat(",###.0").format(new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP));
                }
                case "10001007": { //带千位符2位小数
                    return new DecimalFormat(",###.00").format(new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                case "10001008": { //带千位符3位小数
                    return new DecimalFormat(",###.000").format(new BigDecimal(value).setScale(3, BigDecimal.ROUND_HALF_UP));
                }
                case "10001009": { //百分比取整
                    return new DecimalFormat("#%").format(new BigDecimal(value));
                }
                case "10001010": { //百分比1位小数
                    return new DecimalFormat("0.0%").format(new BigDecimal(value));
                }
                case "10001011": { //百分比2位小数
                    return new DecimalFormat("0.00%").format(new BigDecimal(value));
                }
                case "10001012": { //乘百取整
                    return new DecimalFormat("#").format(new BigDecimal(value).multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                case "10001013": { //乘百1位小数
                    return new DecimalFormat("0.0").format(new BigDecimal(value).multiply(new BigDecimal("100")).setScale(1, BigDecimal.ROUND_HALF_UP));
                }
                case "10001014": { //乘百2位小数
                    return new DecimalFormat("0.00").format(new BigDecimal(value).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
                }
                case "10001015": { //乘百3位小数
                    return new DecimalFormat("0.000").format(new BigDecimal(value).multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP));
                }
                case "10006001": { //yyyy-MM-dd
                    return getDateStr(value, "yyyy-MM-dd");
                }
                case "10006002": { //yyyyMMdd
                    return getDateStr(value, "yyyyMMdd");
                }
                case "10006003": { //yyyy/MM/dd
                    return getDateStr(value, "yyyy/MM/dd");
                }
                case "10006004": { //yyyy-MM
                    return getDateStr(value, "yyyy-MM");
                }
                case "10006005": { //yyyy/MM
                    return getDateStr(value, "yyyy/MM");
                }
                case "10006006": { //yyyyMM
                    return getDateStr(value, "yyyyMM");
                }
                case "10006007": { //yyyy年MM月dd日
                    return getDateStr(value, "yyyy年MM月dd日");
                }
                case "10006008": { //yyyy年MM月
                    return getDateStr(value, "yyyy年MM月");
                }
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static Boolean isNumeric(String value, String regex, String pattern, String replaceStr) {
        String reStr = value.replaceAll(replaceStr, "");
        if (StringUtils.isNotBlank(regex)) {
            return reStr.matches(regex) && new DecimalFormat(pattern).format("%".equals(replaceStr)?
                    new BigDecimal(reStr).divide(new BigDecimal("100")):new BigDecimal(reStr)).equals(value);
        } else {
            return StringUtils.isNumeric(reStr) && new DecimalFormat(pattern).format("%".equals(replaceStr)?
                    new BigDecimal(reStr).divide(new BigDecimal("100")):new BigDecimal(reStr)).equals(value);
        }
    }

    public static boolean isDate(String dateValue, String pattern) {
        if (dateValue == null) {
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        if (dateValue.length() != dateFormat.toPattern().length()) {
            return false;
        }
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateValue);
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }


    public static String getDateStr(String dateValue, String pattern) {
        Date date = castDateByStr(dateValue);
        if (date != null) {
            return DateUtils.dateToStr(date, pattern);
        }
        return "";
    }

    public static Date castDateByStr(String dateValue) {
        Map<String, Object> result = getDateFormat(dateValue);
        DateTimeFormatter dateFormat = (DateTimeFormatter) result.get("formatter");
        if (dateFormat != null) {
            try {
                dateValue = (String) result.get("value");
                Date date = dateFormat.parseDateTime(dateValue).toDate();
                return date;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isRealDate(String strDate) {
        boolean isDate = true;
        DateTimeFormatter formatter = null;
        try {
            if (strDate.contains("/") && strDate.lastIndexOf("/") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
                formatter.parseDateTime(strDate).toDate();
            } else if (strDate.contains(".") && strDate.lastIndexOf(".") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy.MM.dd");
                formatter.parseDateTime(strDate).toDate();
            } else if (strDate.contains("-") && strDate.lastIndexOf("-") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                formatter.parseDateTime(strDate).toDate();
            } else if (strDate.contains("_") && strDate.lastIndexOf("_") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy_MM_dd");
                formatter.parseDateTime(strDate).toDate();
            } else if (strDate.length() == 8 && !strDate.contains("年") && !strDate.contains("日")) {
                formatter = DateTimeFormat.forPattern("yyyyMMdd");
                formatter.parseDateTime(strDate).toDate();
            } else if (strDate.length() > 8 && !strDate.contains("/") && !strDate.contains("-") && !strDate.contains("_") && !strDate.contains(".")) {
                formatter = DateTimeFormat.forPattern("yyyyMMdd");
                strDate = strDate.substring(0, 8);
                formatter.parseDateTime(strDate).toDate();
            } else if (strDate.contains("年") && strDate.contains("日") && strDate.length() == 11) {
                formatter = DateTimeFormat.forPattern("yyyy年MM月dd日");
                formatter.parseDateTime(strDate).toDate();
            } else {
                isDate = false;
            }
        } catch (Exception e) {
            isDate = false;
        }
        return isDate;
    }

    public static boolean isMonth(String strDate) {
        boolean isDate = true;
        DateTimeFormatter formatter = null;
        try {
            if (strDate.contains("/") && strDate.lastIndexOf("/") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
                formatter.parseDateTime(strDate + "/01").toDate();
            } else if (strDate.contains(".") && strDate.lastIndexOf(".") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy.MM.dd");
                formatter.parseDateTime(strDate + ".01").toDate();
            } else if (strDate.contains("-") && strDate.lastIndexOf("-") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                formatter.parseDateTime(strDate + "-01").toDate();
            } else if (strDate.contains("_") && strDate.lastIndexOf("_") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy_MM_dd");
                formatter.parseDateTime(strDate + "_01").toDate();
            } else if (strDate.length() == 6) {
                formatter = DateTimeFormat.forPattern("yyyyMMdd");
                formatter.parseDateTime(strDate + "01").toDate();
            } else if (strDate.contains("年") && !strDate.contains("日") && strDate.length() == 8) {
                formatter = DateTimeFormat.forPattern("yyyy年MM月dd日");
                formatter.parseDateTime(strDate + "01日").toDate();
            }  else {
                isDate = false;
            }
        } catch (Exception e) {
            isDate = false;
        }
        return isDate;
    }


    private static Map<String, Object> getDateFormat(String strDate) {
        Map<String, Object> result = Maps.newHashMap();
        DateTimeFormatter formatter = null;
        try {
            if (strDate.contains("/") && strDate.lastIndexOf("/") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
                if (strDate.length() > 10) {
                    strDate = strDate.substring(0, 10);
                }
            } else if (strDate.contains(".") && strDate.lastIndexOf(".") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy.MM.dd");
                if (strDate.length() > 10) {
                    strDate = strDate.substring(0, 10);
                }
            } else if (strDate.contains("-") && strDate.lastIndexOf("-") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                if (strDate.length() > 10) {
                    strDate = strDate.substring(0, 10);
                }
            } else if (strDate.contains("_") && strDate.lastIndexOf("_") == 7) {
                formatter = DateTimeFormat.forPattern("yyyy_MM_dd");
                if (strDate.length() > 10) {
                    strDate = strDate.substring(0, 10);
                }
            } else if (strDate.length() == 8 && !strDate.contains("年") && !strDate.contains("日")) {
                formatter = DateTimeFormat.forPattern("yyyyMMdd");
            } else if (strDate.length() > 8 && !strDate.contains("/")
                    && !strDate.contains("-") && !strDate.contains("_")
                    && !strDate.contains(".") && !strDate.contains("年") && !strDate.contains("日")) {
                formatter = DateTimeFormat.forPattern("yyyyMMdd");
                strDate = strDate.substring(0, 8);
            } else if (strDate.contains("/") && strDate.lastIndexOf("/") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
                strDate = strDate + "/01";
            } else if (strDate.contains(".") && strDate.lastIndexOf(".") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy.MM.dd");
                strDate = strDate + ".01";
            } else if (strDate.contains("-") && strDate.lastIndexOf("-") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                strDate = strDate + "-01";
            } else if (strDate.contains("_") && strDate.lastIndexOf("_") == 4) {
                formatter = DateTimeFormat.forPattern("yyyy_MM_dd");
                strDate = strDate + "_01";
            } else if (strDate.length() == 6) {
                formatter = DateTimeFormat.forPattern("yyyyMMdd");
                strDate = strDate + "01";
            } else if (strDate.contains("年") && !strDate.contains("日") && (strDate.length() == 8 || strDate.length() == 7)) {
                formatter = DateTimeFormat.forPattern("yyyy年MM月dd日");
                if (strDate.length() == 7) {
                    strDate = strDate.substring(0, 5).concat("0").concat(strDate.substring(5, 7)).concat("01日");
                } else {
                    strDate = strDate + "01日";
                }
            } else if (strDate.contains("年") && strDate.contains("日") && strDate.length() == 11) {
                formatter = DateTimeFormat.forPattern("yyyy年MM月dd日");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("formatter", formatter);
        result.put("value", strDate);
        return result;
    }

    public static String repalceAllFormatter(String dateStr) {
        if (StringUtils.isNotBlank(dateStr)) {
            List<String> strs = Lists.newArrayList("-", "/", "_", "\\.","年","月","日");
            for (String str : strs) {
                dateStr = dateStr.replaceAll(str, "");
            }
            return dateStr;
        }
        return "";
    }

    public static String dateToStr(String formatCode, Date date) {
        if (StringUtils.isBlank(formatCode) || date == null) {
            return "";
        }
        switch (formatCode) {
            case "10006001": { //yyyy-MM-dd
                return DateUtils.dateToStr(date, "yyyy-MM-dd");
            }
            case "10006002": { //yyyyMMdd
                return DateUtils.dateToStr(date, "yyyyMMdd");
            }
            case "10006003": { //yyyy/MM/dd
                return DateUtils.dateToStr(date, "yyyy/MM/dd");
            }
            case "10006004": { //yyyy-MM
                return DateUtils.dateToStr(date, "yyyy-MM");
            }
            case "10006005": { //yyyy/MM
                return DateUtils.dateToStr(date, "yyyy/MM");
            }
            case "10006006": { //yyyyMM
                return DateUtils.dateToStr(date, "yyyyMM");
            }
            case "10006007": {//yyyy年MM月dd日
                return DateUtils.dateToStr(date, "yyyy年MM月dd日");
            }
            case "10006008": {//yyyy年MM月
                return DateUtils.dateToStr(date, "yyyy年MM月");
            }
            default:
                return "";
        }
    }
}
