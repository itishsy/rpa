package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1019", name = "表格读取方式")
public enum ExcelReadType {
    header("按表头名称"),
    multipleHeader("按多表头名称"),
    index("按列索引号"),
    cell("指定单元格"),
    python("python读取");

    String name;
    ExcelReadType(String name){
        this.name = name;
    }
}
