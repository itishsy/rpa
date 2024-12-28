package com.seebon.rpa.entity.robot.enums;

import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 登录方式
 * <p>
 * 社保系统 socialLoginType
 * 养老系统 ylLoginType
 * 医疗系统 medicalLoginType
 * 工伤系统 gsLoginType
 * 单工伤   dgsLoginType
 * 备案系统 baLoginType
 * 税务系统 taxLoginType
 * 金保系统 jbLoginType
 * 失业系统 syLoginType
 * 市网系统  swLoginType
 * 公积金系统 accfundLoginType
 * 国管公积金系统 guoGuanAccfundLoginType
 */
@Getter
public enum LoginTypeEnum {
    socialLoginType("10007001", "社保系统", "socialLoginType"),
    ylLoginType("10007002", "养老系统", "ylLoginType"),
    medicalLoginType("10007003", "医疗系统", "medicalLoginType"),
    dgsLoginType("10007004", "单工伤", "dgsLoginType"),
    gsLoginType("10007005", "工伤系统", "gsLoginType"),
    baLoginType("10007006", "备案系统", "baLoginType"),
    taxLoginType("10007007", "税务系统", "taxLoginType"),
    jbLoginType("10007008", "金保系统", "jbLoginType"),
    syLoginType("10007009", "失业系统", "syLoginType"),
    swLoginType("10007010", "市网系统", "swLoginType"),
    accfundLoginType("10008001", "公积金系统", "accfundLoginType"),
    guoGuanAccfundLoginType("10008002", "国管公积金系统", "guoGuanAccfundLoginType"),
    ;

    private String code;
    private String name;
    private String value;

    LoginTypeEnum(String code, String name, String value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    public static List<String> getValues() {
        List<String> values = Lists.newArrayList();
        for (LoginTypeEnum e : LoginTypeEnum.values()) {
            values.add(e.getValue());
        }
        return values;
    }

    public static String getValueByCode(String declareSystem) {
        for (LoginTypeEnum e : LoginTypeEnum.values()) {
            if (e.getCode().equals(declareSystem)) {
                return e.getValue();
            }
        }
        return null;
    }
}
