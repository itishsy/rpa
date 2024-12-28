package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/12/13  16:45
 */
public enum FileResultEnum {
    INCREASE_OR_DECREASE_PERSONNEL("INCREASE_OR_DECREASE_PERSONNEL", 1),//增减员
    REGISTER_DETAILS("REGISTER_DETAILS", 2),//在册明细
    COST_DETAILS("COST_DETAILS", 3);//费用明细

    private String type;

    private Integer code;

    FileResultEnum(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static FileResultEnum getEnumByCode(String code) {
        FileResultEnum[] values = FileResultEnum.values();
        for (FileResultEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
