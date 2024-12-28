package com.seebon.rpa.entity.robot.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-07 12:28
 */
public enum FlowTypeEnum{
    SURVEY (0,"配置"),
    TEST (1,"调试通过"),
    WAIT (2,"等待数据"),
    CHECK (3,"验证有效"),
    REPAIR (4,"修复问题");


    private String name;

    private Integer code;

    FlowTypeEnum(Integer code,String name){
        this.code=code;
        this.name=name;
    }

    public Integer getCode(){
        return code;
    }

    public String getName(){
        return name;
    }

    public static String getNameByCode(Integer code) {
        FlowTypeEnum[] values = FlowTypeEnum.values();
        for (FlowTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static FlowTypeEnum getEnumByCode(Integer code) {
        FlowTypeEnum[] values = FlowTypeEnum.values();
        for (FlowTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
