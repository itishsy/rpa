package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-18 09:30:59
 */
public enum DeclareStatusEnum {

    Discard(0, "作废"),
    ToBeDeclared(1, "待申报"),
    DeclarationInProgress(2, "申报中"),
    PartiallySuccessful(3, "申报失败（部分）"),
    Successful(4, "申报成功"),
    Failed(5, "申报失败"),
    ToBeSubmitted(6, "待提交"),
    SuccessfulPart(7, "申报成功（部分）");

    private Integer code;

    private String name;

    DeclareStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        DeclareStatusEnum[] values = DeclareStatusEnum.values();
        for (DeclareStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static DeclareStatusEnum getEnumByCode(Integer code) {
        DeclareStatusEnum[] values = DeclareStatusEnum.values();
        for (DeclareStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
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
