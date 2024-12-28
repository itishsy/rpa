package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author LY
 * @date 2023/12/11 9:59
 * 回盘费用明细类型
 */

@DataDictKey(value = "5168", name = "回盘费用明细类型")
public enum CostType {

    CostTypeExcel("回盘-excel"),
    CostTypeTable("回盘-网页表格");


    String name;


    CostType (String name){
        this.name = name;
    }
}
