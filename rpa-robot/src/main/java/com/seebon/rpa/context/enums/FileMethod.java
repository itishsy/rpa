package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1012", name = "文件操作方法")
public enum FileMethod {
    copy("复制"),
    get("获取"),
    find("查找"),
    rename("重命名"),
    createPath("创建目录"),
    createFile("创建文件"),
    createExcel("创建Excel文件"),
    delete("删除"),
    csv2Excel("csv转excel"),
    excel2Img("excel转图片"),
    pdf2Img("pdf转图片"),
    base642File("base64转文件"),
    excelInsertPicture("excel插入图片"),
    WordInsertPicture("word插入图片"),
    wordToImg("word转图片2"),
    word2Img("word转图片"),
    unzip("解压缩"),
    open("打开"),
    close("关闭"),
    zip("压缩文件夹"),
    downloadNewFile("下载的最新文件");

    String name;
    FileMethod(String name){
        this.name = name;
    }
}
