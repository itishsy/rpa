package com.seebon.rpa.entity.robot.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-07 12:15
 */
public enum RobotAppTypeEnum{
    SURVEY (0,"调研"),
    CONFIGURATION (1,"配置"),
    TEST (2,"调试通过"),
    WAIT (3,"等待数据"),
    CHECK (4,"验证有效"),
    NORMAL (5,"正常运行"),
    REPAIR (6,"修复问题");


    private String name;

    private Integer code;

    RobotAppTypeEnum(Integer code,String name){
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
        RobotAppTypeEnum[] values = RobotAppTypeEnum.values();
        for (RobotAppTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static RobotAppTypeEnum getEnumByCode(Integer code) {
        RobotAppTypeEnum[] values = RobotAppTypeEnum.values();
        for (RobotAppTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
