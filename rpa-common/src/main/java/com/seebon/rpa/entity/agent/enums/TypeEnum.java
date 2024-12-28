package com.seebon.rpa.entity.agent.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-09-01 10:46
 */
public enum TypeEnum{
    SOCIAL("社保", 1),
    ACCFUND("公积金", 2),
    PERSON("人员信息", 3);

    private String name;

    private Integer code;

    TypeEnum(String name,Integer code) {
        this.code = code;
        this.name = name;
    }

    public static Integer getNameByCode(String name) {
        TypeEnum[] values = TypeEnum.values();
        for (TypeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
