package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1021", name = "数据库执行类型")
public enum DbExeType {
    selectList("列表查询"),
    checkList("核验查询"),
    returnList("回盘查询"),
    singleRow("查询单行"),
    update("数据更新"),
    batchUpdate("批量更新"),
    batchAdd("批量新增"),
    ;

    String name;

    DbExeType(String name) {
        this.name = name;
    }
}
