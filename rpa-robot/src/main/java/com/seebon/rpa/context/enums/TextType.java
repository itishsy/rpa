package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1053", name = "文本方式")
public enum TextType {

    text("文本"),
    ocr_text("OCR识别"),
    ocr_baidu_text("OCR百度识别"),;

    String name;

    TextType(String name) {
        this.name = name;
    }
}
