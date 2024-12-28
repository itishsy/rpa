package com.seebon.rpa.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 自定义的Cron表达式处理
 * 根据时间的yyyy,MM,dd,HH(hh),mm,ss进行匹配。
 */
public class CronUtil {

    /**
     * 用cron表达式来匹配时间data
     *
     * @param cron 规则：{dd:"14-15",HH:"23,24"}
     * @param date
     * @return
     */
    public static boolean matchAll(String cron, Date date) {
        Map<String, String> map = JSON.parseObject(cron, Map.class);
        for (String key : map.keySet()) {
            String value = new SimpleDateFormat(key).format(date);
            if (!match(map.get(key), value)) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchOne(String cron, Date date) {
        Map<String, String> map = JSON.parseObject(cron, Map.class);
        for (String key : map.keySet()) {
            String value = new SimpleDateFormat(key).format(date);
            if (match(value, map.get(key))) {
                return true;
            }
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
        if (cronStr.contains("-")) {
            int start = Integer.parseInt(cronStr.split("-")[0]);
            int end = Integer.parseInt(cronStr.split("-")[1]);
            return (Integer.parseInt(value) >= start && Integer.parseInt(value) <= end);
        } else if (cronStr.contains(",")) {
            List<String> list = Arrays.asList(cronStr.split(","));
            return list.contains(value);
        }
        return cronStr.equals(value);
    }

    public static void main(String[] args) {
        System.out.println(matchAll("{dd:\"14-20\",HH:\"19-24\"}", new Date()));
    }
}
