package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author LY
 * @date 2023/11/7 14:26
 */

@DataDictKey(value = "1671", name = "回盘类型")
public enum CounterOfferType {
    CounterOfferSingle("回盘-通知形式"),
    CounterOfferExcel("回盘-excel文件"),
    CounterOfferTable("回盘-网页表格"),
    CounterOfferNull("回盘-无返回结果");


    String name;


    CounterOfferType(String name) {
        this.name = name;
    }
}
