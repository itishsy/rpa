package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/12/13  17:39
 */
public enum NoCharLineEnum {

    one(1,"one"),
    two(2,"two"),
    three(3,"three"),
    four(4,"four"),
    five(5,"five"),
    six(6,"six"),
    seven(7,"seven"),
    eight(8,"eight"),
    nine(9,"nine"),
    ten(10,"ten");

    Integer name;

    String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getName() {
        return name;
    }

    public static String getEnumByCode(Integer name) {
        NoCharLineEnum[] values = NoCharLineEnum.values();
        for (NoCharLineEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }


    public void setName(Integer name) {
        this.name = name;
    }

    NoCharLineEnum(Integer name,String code){
        this.name = name;
        this.code = code;
    }
}
