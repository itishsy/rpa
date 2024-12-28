package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author : 阿祥
 * @desc : 回盘在册明细类型
 * @date : 2023/12/8  18:18
 */
@DataDictKey(value = "3786", name = "回盘类型")
public enum OfferRegisterDetailsType {
    OfferRegisterDetailsExcel("回盘-excel文件"),
    OfferRegisterDetailsTable("回盘-网页表格");


    String name;


    OfferRegisterDetailsType(String name) {
        this.name = name;
    }
}
