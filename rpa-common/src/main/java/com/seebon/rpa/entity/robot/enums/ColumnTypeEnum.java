package com.seebon.rpa.entity.robot.enums;

import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 条件字段
 * <p>
 * (1-应用名称、2-申报账户、3-申报系统、4-事项、5-登录方式)
 */
@Getter
public enum ColumnTypeEnum {
    appCode(1, "应用名称", "appCode"),
    taskCode(2, "申报账户", "taskCode"),
    declareSystem(3, "申报系统", "declareSystem"),
    queueItem(4, "事项", "queueItem"),
    loginType(5, "登录方式", "loginType"),
    ;

    private Integer code;
    private String name;
    private String value;

    ColumnTypeEnum(Integer code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public static List<String> getValues() {
        List<String> values = Lists.newArrayList();
        for (ColumnTypeEnum e : ColumnTypeEnum.values()) {
            values.add(e.getValue());
        }
        return values;
    }

    public static String getValueByCode(Integer code) {
        for (ColumnTypeEnum e : ColumnTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getValue();
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        for (ColumnTypeEnum e : ColumnTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return null;
    }
}
