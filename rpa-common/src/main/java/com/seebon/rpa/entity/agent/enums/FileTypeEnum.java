package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-27 15:42:32
 */
public enum FileTypeEnum {

    CopyOfIdCard("10014001", "身份证复印件"),
    PersonalPhoto("10014002", "个人照片");

    private String code;

    private String name;

    FileTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code) {
        FileTypeEnum[] values = FileTypeEnum.values();
        for (FileTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static String getCodeByName(String name) {
        FileTypeEnum[] values = FileTypeEnum.values();
        for (FileTypeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static FileTypeEnum getEnumByCode(String code) {
        FileTypeEnum[] values = FileTypeEnum.values();
        for (FileTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
