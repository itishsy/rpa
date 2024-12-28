package com.seebon.rpa.actions.impl.tool;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.DateUtils;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.DateMethod;
import com.seebon.rpa.context.runtime.RobotConfigException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@ActionUtils
@RobotAction(name = "日期操作", targetType = ObjectTarget.class, comment = "日期操作")
public class DateUtil extends AbstractAction {

    @ActionArgs(value = "操作类型", required = true, dict = DateMethod.class)
    private String actionType;

    @ActionArgs(value = "操作参数")
    private String actionArgs;

    @ActionArgs(value = "结果变量")
    private String resultKey;

    @Override
    public void run() {
        switch (DateMethod.valueOf(actionType)) {
            case getNow: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                ctx.setVariable(resultKey, getNow(actionArgs));
                break;
            }
            case getLastMonth: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                ctx.setVariable(resultKey, getLastMonth(actionArgs));
                break;
            }
            case getLastDayOfLastMonth: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                ctx.setVariable(resultKey, getLastDayOfLastMonth(actionArgs));
                break;
            }
            case getLastMonthDay: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                ctx.setVariable(resultKey, getLastMonthDay());
                break;
            }
            case getLastMonthFirstDay: {
                ctx.setVariable(resultKey, this.getLastMonthFirstDay(actionArgs));
                break;
            }
            case addMonth: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                JSONObject json = JSON.parseObject(actionArgs);
                ctx.setVariable(resultKey, addMonth(json.getString("time"), json.getInteger("offset"), json.getString("format")));
                break;
            }
            case addDay: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                JSONObject json = JSON.parseObject(actionArgs);
                ctx.setVariable(resultKey, addDay(json.getString("time"), json.getInteger("offset")));
                break;
            }
            case getCurrentMonthFirstDay: {
                SimpleDateFormat format = new SimpleDateFormat(actionArgs);
                Calendar firstDate = Calendar.getInstance();
                Date date = new Date();
                firstDate.setTime(date);
                firstDate.set(Calendar.DATE, 1);
                firstDate.set(Calendar.HOUR_OF_DAY, 00);
                firstDate.set(Calendar.MINUTE, 00);
                firstDate.set(Calendar.SECOND, 00);
                firstDate.set(Calendar.MILLISECOND, 000);
                ctx.setVariable(resultKey, format.format(firstDate.getTime()));
                break;
            }
            case getCurrentMonthLastDay: {
                String lastDay = this.getCurrentMonthLastDay(actionArgs);
                ctx.setVariable(resultKey, lastDay);
                break;
            }

            /**
             * 拿到[操作参数]所在月份的当前天
             * 例：[操作参数]传入20230201，当前日期为20230315，则返回20230216。如果该日期不存在，则取[操作参数]所在月最后一天
             */
            case getNowDayByAssignDate: {
                // 参数日期
                String variableName = getTarget();
                String paramDateStr = (String) parseObject(variableName);
                Date paramDate = DateUtils.strToDate(paramDateStr, actionArgs);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(paramDate); // 在此处设置指定日期
                calendar.add(Calendar.DAY_OF_MONTH, -29); // 减去29天
                Date newDate = calendar.getTime();
                ctx.setVariable(resultKey, DateUtils.dateToStr(newDate, actionArgs));
                break;
            }

            case getNowMonthCh: {
                LocalDate today = LocalDate.now();
                LocalDate previousMonth = today.minusMonths(1);
                String monthName = previousMonth.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.CHINA);
                ctx.setVariable(resultKey, monthName);
                break;
            }

            case getNowMonth: {
                LocalDate currentDate = LocalDate.now();
                Month currentMonth = currentDate.getMonth();
                ctx.setVariable(resultKey, currentMonth);
                break;
            }

            case getLastMonthCH: {
                Month currentMonth = Month.values()[LocalDate.now().getMonthValue() - 1];
                String monthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.CHINA);
                ctx.setVariable(resultKey, monthName);
                break;
            }

            case getNextMonthCH: {
                Month currentMonth = Month.values()[LocalDate.now().getMonthValue()];
                String monthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.CHINA);
                ctx.setVariable(resultKey, monthName);
                break;
            }
            case getMonthCH: {
                Month currentMonth = Month.values()[Integer.parseInt(actionArgs) - 1];
                String monthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.CHINA);
                ctx.setVariable(resultKey, monthName);
                break;
            }

            case getLastYearToday: {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(actionArgs);
                LocalDate today = LocalDate.now();
                LocalDate lastYearToday = today.minusYears(1);
                ctx.setVariable(resultKey, lastYearToday.format(formatter).toString());
                break;
            }

            case getLastYearMonthToday: {
                ctx.setVariable(resultKey, getLastYearMonthToday());
                break;
            }

            case getDayFirstDay: {
                LocalDate date = LocalDate.parse(getTarget(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalDate firstDayOfMonth = date.withDayOfMonth(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(actionArgs);
                String firstDayOfMonthString = firstDayOfMonth.format(formatter);
                ctx.setVariable(resultKey, firstDayOfMonthString);
                System.out.println(firstDayOfMonthString + "_____________");
                break;
            }
            case getMonthLastDay: {
                String variableName = getTarget();
                String paramDateStr = (String) parseObject(variableName);
                if (paramDateStr.length() == 7) {
                    StringBuilder builder = new StringBuilder();
                    StringBuilder append = builder.append(paramDateStr).append("-01");
                    paramDateStr = append.toString();
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(actionArgs);
                LocalDate date = LocalDate.parse(paramDateStr, formatter);
                YearMonth yearMonth = YearMonth.from(date.minusMonths(1));
                LocalDate lastDayOfLastMonth = yearMonth.atEndOfMonth();
                ctx.setVariable(resultKey, lastDayOfLastMonth.toString());
                System.out.println(lastDayOfLastMonth + "----------");
                break;
            }

            // 获取前几个月或后几个月的日期，支持自定义格式
            case getOffsetMonth: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                JSONObject json = JSON.parseObject(actionArgs);
                ctx.setVariable(resultKey, addMonthFormat(json.getString("time"), json.getInteger("offset"), json.getString("format")));
                break;
            }
            case getNextMonth: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                LocalDate today = LocalDate.now();
                LocalDate firstDayOfNextMonth = today.plusMonths(1).withDayOfMonth(1);
                String dateFormat = actionArgs;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
                String formattedDate = firstDayOfNextMonth.format(dtf);
                ctx.setVariable(resultKey, formattedDate);
                break;
            }
            case getNextYearMonth: {
                Date yearMonth = cn.hutool.core.date.DateUtil.nextMonth();
                String format = cn.hutool.core.date.DateUtil.format(yearMonth, "yyyy-MM");
                ctx.setVariable(resultKey, format);
                break;
            }
            case getNextMonthByCon: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("日期操作方法，参数不能为空");
                }
                LocalDate today = LocalDate.parse(getTarget(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalDate firstDayOfNextMonth = today.plusMonths(1).withDayOfMonth(1);
                String dateFormat = actionArgs;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
                String formattedDate = firstDayOfNextMonth.format(dtf);
                ctx.setVariable(resultKey, formattedDate);
                break;
            }
            default:
                break;
        }
    }

    public String getNow(String format) {
        return cn.hutool.core.date.DateUtil.format(new Date(), format);
    }

    public String getLastMonth(String format) {
        if (StringUtils.isBlank(format)) {
            format = "MM";
        }
        return cn.hutool.core.date.DateUtil.format(cn.hutool.core.date.DateUtil.lastMonth(), format);
    }

    public String getLastMonthDay() {
        DateTime endOfMonth = cn.hutool.core.date.DateUtil.endOfMonth(cn.hutool.core.date.DateUtil.lastMonth());
        return cn.hutool.core.date.DateUtil.format(endOfMonth, "dd");
    }

    public String getLastDayOfLastMonth(String format) {
        DateTime endOfMonth = cn.hutool.core.date.DateUtil.endOfMonth(cn.hutool.core.date.DateUtil.lastMonth());
        return cn.hutool.core.date.DateUtil.format(endOfMonth, format);
    }

    public String getLastDayOfMonth(String pattern, String month, String monthPattern) {
        SimpleDateFormat format = new SimpleDateFormat(monthPattern);
        try {
            DateTime endOfMonth = cn.hutool.core.date.DateUtil.endOfMonth(format.parse(month));
            return cn.hutool.core.date.DateUtil.format(endOfMonth, pattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String addMonth(String time, int offset, String format) {
        DateTime afterDate = cn.hutool.core.date.DateUtil.offsetMonth(time != null ? cn.hutool.core.date.DateUtil.parseDate(time) : new Date(), offset);
        return cn.hutool.core.date.DateUtil.format(afterDate, StringUtils.isBlank(format) ? "yyyy-MM-dd" : format);
    }

    public String addDay(String time, int offset) {
        DateTime afterDate = cn.hutool.core.date.DateUtil.offsetDay(cn.hutool.core.date.DateUtil.parse(time), offset);
        return cn.hutool.core.date.DateUtil.formatDate(afterDate);
    }

    public String getDateString(String pattern, int monthAdd, Integer dateNo, Boolean lastDay) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar firstDate = Calendar.getInstance();
        Date date = new Date();
        firstDate.setTime(date);
        firstDate.add(Calendar.MONTH, monthAdd);
        if (lastDay != null && lastDay) {
            firstDate.set(Calendar.DATE, firstDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (dateNo != null) {
            firstDate.set(Calendar.DATE, dateNo);
        }
        return format.format(firstDate.getTime());
    }

    public String getDateStrByDate(String pattern, String dateStr, int monthAdd, Integer dateNo, Boolean lastDay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Calendar firstDate = Calendar.getInstance();
            Date date = format.parse(parse(dateStr, String.class));
            firstDate.setTime(date);
            firstDate.add(Calendar.MONTH, monthAdd);
            if (lastDay != null && lastDay) {
                firstDate.set(Calendar.DATE, firstDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else if (dateNo != null) {
                firstDate.set(Calendar.DATE, dateNo);
            }
            return format.format(firstDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public String getNowYM(String pattern, int dType, int add) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(dType, add);
        return format.format(calendar.getTime());
    }

    public boolean yearBigThan(String pattern, String dateStr1, String dateStr2, int than) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        try {
            Date date1 = format.parse(parse(dateStr1, String.class));
            Date date2 = format.parse(parse(dateStr2, String.class));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.add(Calendar.YEAR, than);
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            return date2.compareTo(calendar.getTime()) >= 0;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int dateCompareTo(String pattern, String dateStr1, String dateStr2) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date1 = format.parse(parse(dateStr1, String.class));
            Date date2 = format.parse(parse(dateStr2, String.class));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            return date2.compareTo(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getLastDay(String pattern, String date) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date date1 = format.parse(parse(date, String.class));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            return format.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getCurrentDateInfo(int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return type == 2 ? calendar.get(type) + 1 : calendar.get(type);
    }

    public int getMonthsBetween(String date1, String date2, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Calendar firstCal = Calendar.getInstance();
            firstCal.setTime(format.parse(date1));
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(format.parse(date2));
            int firstYear = firstCal.get(Calendar.YEAR);
            int firstMonth = firstCal.get(Calendar.MONTH) + 1;
            int endYear = endCal.get(Calendar.YEAR);
            int endMonth = endCal.get(Calendar.MONTH) + 1;
            return (endYear - firstYear) * 12 + (endMonth - firstMonth) + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getLastYearMonthToday() {
        LocalDate today = LocalDate.now();
        LocalDate lastYearToday = today.minusYears(1);
        LocalDate localDate = lastYearToday.minusMonths(1);
        return localDate.toString();
    }

    /**
     * @param time   （比较时间-offset）
     * @param time1  被对比时间
     * @param offset 时间阈值
     * @return
     */
    public Integer compareTime(String time, String time1, Integer offset) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        try {
            Date parse1 = null;
            if (offset != 0) {
                Date date1 = format.parse(parse(time, String.class));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                calendar.add(Calendar.MONTH, -offset);
                parse1 = calendar.getTime();
            } else {
                parse1 = format.parse(time);
            }

            Date parse2 = format.parse(time1);

            long month = cn.hutool.core.date.DateUtil.betweenMonth(parse1, parse2, false);
            int res = (int) month;
            if (res > 0) {
                return res;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getWeekDay() {
        String[] weekDays = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        //获取当前时间
        LocalDateTime endDate = LocalDateTime.now();
        //获取当前周
        Integer week = endDate.getDayOfWeek().getValue();
        return weekDays[week - 1];

    }

    public String getMonth(String month1, String month2, String pattern) {
        List<String> result = Lists.newArrayList();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            Date beginDate = format.parse(month1);
            result.add(DateUtils.dateToStr(beginDate, pattern));
            Date endDate = format.parse(month2);
            Date start = DateUtils.getDate(Calendar.MONTH, 1, beginDate);
            while (endDate.after(start)) {
                result.add(DateUtils.dateToStr(start, pattern));
                start = DateUtils.getDate(Calendar.MONTH, 1, start);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CollectionUtils.isEmpty(result) ? "" : result.get(0).concat(",").concat(result.get(result.size() - 1));
    }

    public String addMonthFormat(String time, int offset, String format) {
        DateTime afterDate = cn.hutool.core.date.DateUtil.offsetMonth(time != null ? cn.hutool.core.date.DateUtil.parseDate(time) : new Date(), offset);
        if (StringUtils.isNotBlank(format)) {
            return DateUtils.dateToStr(afterDate, format);
        }
        return cn.hutool.core.date.DateUtil.formatDate(afterDate);
    }

    public String intMonth2Chinese(String month) {
        switch (month) {
            case "01":
                return "一";
            case "02":
                return "二";
            case "03":
                return "三";
            case "04":
                return "四";
            case "05":
                return "五";
            case "06":
                return "六";
            case "07":
                return "七";
            case "08":
                return "八";
            case "09":
                return "九";
            case "10":
                return "十";
            case "11":
                return "十一";
            case "12":
                return "十二";
            default:
                return "";
        }
    }

    public String getLastMonthFirstDay(String actionArgs) {
        SimpleDateFormat format = new SimpleDateFormat(actionArgs);
        Calendar lastMonth = Calendar.getInstance();
        lastMonth.setTime(new Date());
        lastMonth.add(Calendar.MONTH, -1);
        Date date = lastMonth.getTime();
        lastMonth.setTime(date);
        lastMonth.set(Calendar.DATE, 1);
        lastMonth.set(Calendar.HOUR_OF_DAY, 00);
        lastMonth.set(Calendar.MINUTE, 00);
        lastMonth.set(Calendar.SECOND, 00);
        lastMonth.set(Calendar.MILLISECOND, 000);
        return format.format(lastMonth.getTime());
    }

    public String getCurrentMonthLastDay(String actionArgs) {
        SimpleDateFormat format = new SimpleDateFormat(actionArgs);
        Calendar firstDate = Calendar.getInstance();
        Date date = new Date();
        firstDate.setTime(date);
        firstDate.set(Calendar.DATE, firstDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        firstDate.set(Calendar.HOUR_OF_DAY, 00);
        firstDate.set(Calendar.MINUTE, 00);
        firstDate.set(Calendar.SECOND, 00);
        firstDate.set(Calendar.MILLISECOND, 000);
        return format.format(firstDate.getTime());
    }

    public String getEffectiveYear() {
        String getLastYearMonthToday = getLastYearMonthToday();
        String lastMonth = getLastMonth("MM");
        if (Integer.parseInt(lastMonth) <= 6) {
            return getLastYearMonthToday.substring(0, 4);
        }
        if (Integer.parseInt(lastMonth) > 6) {
            return getNow("YYYY");
        }
        return "";
    }
}


