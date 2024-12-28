package com.seebon.rpa.entity.robot.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe 服务项目枚举
 * @date 2024-01-10 14:46
 */
public enum ServiceItemTypeEnum {
    ADD(1, "增员"),
    REDUCE(2, "减员"),
    TRANSFER(3, "调基"),
    SUPPLEMENT(5, "补缴"),
    PAY(6, "缴费"),
    REGISTER(7, "在册名单"),
    COST(8, "费用明细"),
    POLICY_NOTICE(9, "政策通知"),
    BASE_DECLARE(10, "基数申报"),
    LOGIN(11, "登录"),
    CHECK_RESULT(12, "查审核结果");

    private String name;

    private Integer code;

    ServiceItemTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(Integer code) {
        ServiceItemTypeEnum[] values = ServiceItemTypeEnum.values();
        for (ServiceItemTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Integer getCodeByName(String name) {
        ServiceItemTypeEnum[] values = ServiceItemTypeEnum.values();
        for (ServiceItemTypeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static ServiceItemTypeEnum getEnumByCode(Integer code) {
        ServiceItemTypeEnum[] values = ServiceItemTypeEnum.values();
        for (ServiceItemTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
