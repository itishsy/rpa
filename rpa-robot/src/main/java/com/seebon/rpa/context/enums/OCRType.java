package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-04 11:18:36
 */
@DataDictKey(value = "1033", name = "图像识别类型")
public enum OCRType {
    pureNumbers("纯数字(收费)", "1"),
    pureEnglish("纯英文(收费)", "2"),
    englishAndNumber("英数混合(收费)", "3"),
    englishAndNumberMore("英数混合_多位(收费)", "1003"),
    calculate("计算题(收费)", "11"),
    autonomousRecognition("自主识别", "4"),
    localRecognition("本地识别", "5"),
    ddddocrRecognition("ddddocr识别", "6");

    String name;
    String code;

    OCRType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static String getCodeByName(String name) {
        OCRType[] values = OCRType.values();
        for (OCRType item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
