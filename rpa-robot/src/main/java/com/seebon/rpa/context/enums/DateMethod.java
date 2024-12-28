package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1024", name = "日期操作方法")
public enum DateMethod {
    getNow("获取当前时间"),
    getCurrentMonthFirstDay("获取本月第一天"),
    getCurrentMonthLastDay("获取本月最后一天"),
    getLastMonth("获取上个月月份"),
    getLastMonthDay("获取上个月最后一天"),
    getLastMonthFirstDay("获取上个月第一天"),
    getLastDayOfLastMonth("获取上个月最后一天的日期"),
    addMonth("增加月份"),
    addDay("增加天"),
    getNowDayByAssignDate("获取指定月份的当前日期"),
    getNowMonthCh("获取上个月份中文名称"),
    getNowMonth("获取当前月份"),
    getLastMonthCH("获取当前月份中文名称"),
    getMonthCH("获取指定月份的中文名称"),
    getNextMonthCH("获取下个月份中文名称"),
    getDayFirstDay("获取指定日期当月的第一天"),
    getMonthLastDay("获取指定月份的最后一天"),
    getLastYearToday("获取去年的今天"),
    getLastYearMonthToday("获取去年的上个月"),
    getOffsetMonth("获取前几个月或后几个月的日期"),
    getNextMonth("获取下一个月的第一天"),
    getNextYearMonth("获取下一个月的年月"),
    getNextMonthByCon("根据提供的日期获取下个月");


    String name;

    DateMethod(String name) {
        this.name = name;
    }
}
