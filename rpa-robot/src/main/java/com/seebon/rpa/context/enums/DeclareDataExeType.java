package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1061", name = "申报数据操作类型")
public enum DeclareDataExeType {
    selectIdCardByCond("查询人员身份证"),
    selectByCond("按条件查询"),
    updateFailedByIdCard("按人员更新失败原因"),
    updateByCond("更新字段"),
    declareFailed("申报失败"),
    declareing("申报中"),
    ;

    String name;

    DeclareDataExeType(String name) {
        this.name = name;
    }
}
