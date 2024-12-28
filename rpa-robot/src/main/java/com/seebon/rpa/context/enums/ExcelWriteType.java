package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1025", name = "表格读取方式")
public enum ExcelWriteType {
    removeRow("移除行"),
    removeRowByCond("按条件移除行"),
    removeCellByCond("按条件删除单元格"),
    removeColumn("移除列"),
    updateColumn("更新列"),
    updateCell("更新单元格"),
    updateColumnType("更新列单元格格式"),
    updateCellByCondition("根据条件更新单元格"),
    deleteColumn("删除列"),
    updateCellFormat("更新单元格格式"),
    resaveExcel("重新保存excel");

    String name;
    ExcelWriteType(String name){
        this.name = name;
    }
}
