package com.seebon.rpa.entity.agent.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-08-31 10:08
 */
public enum AttributeEnum{
    WENBEN(1, "1100020001","文本"),
    XIALA(2, "1100020004","下拉"),
    SHUZHI(3,"1100020003","数值"),
    NIANYUERI(4, "1100020002","年月日"),
    NIANYU(5, "1100020005","年月");

    private Integer code;
    private String name;
    private String type;

    AttributeEnum(Integer code,String type,String name) {
        this.code = code;
        this.type=type;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static String getNameByCode(Integer code) {
        AttributeEnum[] values = AttributeEnum.values();
        for (AttributeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }
    public static String getTypeByCode(Integer code) {
        AttributeEnum[] values = AttributeEnum.values();
        for (AttributeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getType();
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        AttributeEnum[] values = AttributeEnum.values();
        for (AttributeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static AttributeEnum getEnumByCode(Integer code) {
        AttributeEnum[] values = AttributeEnum.values();
        for (AttributeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
