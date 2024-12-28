package com.seebon.rpa.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-28 11:36:12
 */
public class ScheduleUtils {

    public static boolean matchSchedule(String execCycle, String execAreaTime, Integer execStyle, Date date) {
        if (StringUtils.isBlank(execCycle) || execStyle == null) {
            return false;
        }
        String value = DateFormatUtils.format(date, "dd");
        if (match(execCycle, value)) {
            if (execStyle == 3) { //实时
                return true;
            }
            if (StringUtils.isBlank(execAreaTime)) {
                return false;
            }
            String cron = "";
            if (execStyle == 2) {//固定时间
                List<String> times = Lists.newArrayList(execAreaTime.split(",")).stream().map(time -> {
                    return time.replace(":", "");
                }).collect(Collectors.toList());
                times.sort((o1, o2) -> Integer.parseInt(o1) - Integer.parseInt(o2));
                cron = StringUtils.join(times, ",");
            } else if (execStyle == 1) {//连续时间
                List<String> times = Lists.newArrayList(execAreaTime.split("-")).stream().map(time -> {
                    return time.split(":")[0];
                }).collect(Collectors.toList());
                times.sort((o1, o2) -> Integer.parseInt(o1) - Integer.parseInt(o2));
                cron = StringUtils.join(times, "-");
            }
            if (execStyle == 2) {
                value = DateFormatUtils.format(date, "HHmm");
            } else {
                value = DateFormatUtils.format(date, "HH");
            }
            return match(cron, value);
        }
        return false;
    }

    /**
     * 字符串中的数字匹配
     *
     * @param cronStr 连续：1-3，散列：3,6
     * @param value
     * @return
     */
    public static boolean match(String cronStr, String value) {
        if (StringUtils.isBlank(cronStr)) {
            return false;
        }
        if (cronStr.contains(",")) {
            try {
                List<String> list = Arrays.asList(cronStr.split(","));
                for (String str : list) {
                    if (match(str, value)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        } else if (cronStr.contains("-")) {
            try {
                int start = Integer.parseInt(cronStr.split("-")[0]);
                int end = Integer.parseInt(cronStr.split("-")[1]);
                return (Integer.parseInt(value) >= start && Integer.parseInt(value) <= end);
            } catch (Exception e) {
                return false;
            }
        }
        if (cronStr.equals(value)) {
            return true;
        }
        try {
            return Integer.parseInt(cronStr) == Integer.parseInt(value);
        } catch (Exception e) {
            return false;
        }
    }
}
