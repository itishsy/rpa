package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;
import lombok.Getter;

/**
 * ocr解析源
 */
@DataDictKey(value = "1981", name = "ocr解析源")
public enum OcrOriginType {

    SELF_DDDC_OCR("dddc_OCR"),
    SELF_BAIDU_OCR("百度OCR识别");


    private String name;
    OcrOriginType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
