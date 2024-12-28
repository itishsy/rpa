package com.seebon.rpa.entity.robot.enums;

/**
 * 执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：在册名单，8：费用明细，9：政策通知 10：基数申报 11：登录 12：查审核结果
 */
public enum QueueItemTypeEnum {
    ADD(1, "增减员"),
    PAY(6, "缴费"),
    REGISTER(7, "在册名单"),
    COST(8, "费用明细"),
    POLICY_NOTICE(9, "政策通知"),
    BASE_DECLARE(10, "基数申报"),
    LOGIN(11, "登录"),
    CHECK_RESULT(12, "查审核结果");

    private String name;

    private Integer code;

    QueueItemTypeEnum(Integer code, String name) {
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
        QueueItemTypeEnum[] values = QueueItemTypeEnum.values();
        for (QueueItemTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static Integer getByCodeName(String name) {
        QueueItemTypeEnum[] values = QueueItemTypeEnum.values();
        for (QueueItemTypeEnum item : values) {
            if (item.getName().equals(name)) {
                return item.getCode();
            }
        }
        return null;
    }

    public static QueueItemTypeEnum getEnumByCode(Integer code) {
        QueueItemTypeEnum[] values = QueueItemTypeEnum.values();
        for (QueueItemTypeEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
