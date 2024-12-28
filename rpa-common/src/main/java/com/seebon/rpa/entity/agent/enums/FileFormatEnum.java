package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-28 09:08:41
 */
public enum FileFormatEnum {

    AnyFile(1, "文件类型不限", ""),
    AnyImage(2, "图片类型不限", "jpg png jpeg tif tiff gif pcx tga exif fpx svg psd cdr pcd dxf ufo eps ai raw wmf webp avif apng hdri ico emf flic"),
    JpgImage(3, "JPG图片", "jpg");

    private Integer code;

    private String text;

    private String format;

    FileFormatEnum(Integer code, String text, String format) {
        this.code = code;
        this.text = text;
        this.format = format;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public String getFormat() {
        return format;
    }

    public static String getTextByCode(Integer code) {
        FileFormatEnum[] values = FileFormatEnum.values();
        for (FileFormatEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getText();
            }
        }
        return "";
    }

    public static Integer getCodeByName(String text) {
        FileFormatEnum[] values = FileFormatEnum.values();
        for (FileFormatEnum item : values) {
            if (item.getText().equals(text)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static String getFormatByCode(Integer code) {
        FileFormatEnum[] values = FileFormatEnum.values();
        for (FileFormatEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getFormat();
            }
        }
        return "";
    }
}
