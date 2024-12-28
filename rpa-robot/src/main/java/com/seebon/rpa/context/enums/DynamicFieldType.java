package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1016", name = "动态字段类型")
public enum DynamicFieldType {
    text("文本框"),
    password("密码框"),
    number("数值框"),
    date("日期选择框"),
    singleDropList("下拉单选框"),
    multipleDropList("下拉多选框"),
    single("单选框"),
    multiple("复选框"),
    photoUpload("图片上传"),
    fileUpload("附件上传");

    String name;
    DynamicFieldType(String name){
        this.name = name;
    }
}
