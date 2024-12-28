package com.seebon.rpa.entity.robot.enums;

/**
 * TODO
 *
 * @author zjf
 * @describ 应用阶段状态
 * @date 2024-01-07 12:10
 */
public enum RobotStageEnum{
    CONFIGURATION (0,"配置"),
    TEST (1,"测试"),
    OPERATION (2,"运维");


    private String name;

    private Integer code;

    RobotStageEnum(Integer code,String name){
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
        RobotStageEnum[] values = RobotStageEnum.values();
        for (RobotStageEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static RobotStageEnum getEnumByCode(Integer code) {
        RobotStageEnum[] values = RobotStageEnum.values();
        for (RobotStageEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
