package com.seebon.rpa.entity.robot.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe 发布状态枚举
 * @date 2024-01-10 14:33
 */
public enum  ReleaseTypeEnum{
    UNRELEASED (0,"未发布"),
    RELEASED (1,"已发布"),
    UPDATE (2,"有更新未发布");


    private String name;

    private Integer code;

    ReleaseTypeEnum(Integer code,String name){
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
        ReleaseTypeEnum[] values = ReleaseTypeEnum.values();
        for (ReleaseTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static ReleaseTypeEnum getEnumByCode(Integer code) {
        ReleaseTypeEnum[] values = ReleaseTypeEnum.values();
        for (ReleaseTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
