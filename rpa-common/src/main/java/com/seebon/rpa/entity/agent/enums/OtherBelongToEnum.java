package com.seebon.rpa.entity.agent.enums;

import lombok.Getter;

/**
 * @Author: tanyong
 * @Description:
 * @Date: 2024/9/26 17:15
 * @Modified By:
 */
@Getter
public enum OtherBelongToEnum {
    BLANK_TRIM("去空格", "10036001", "", ""),
    PROVINCE("省", "10036002", "^(\\d{2}).*", "0000"),
    CITY("市", "10036003", "^(\\d{4}).*", "00"),
    DISTRICT("区", "10036004", "^(\\d{6}).*", "");

    private final String code;
    private final String name;
    private final String regexStr;
    private final String suppleStr;

    OtherBelongToEnum(String name, String code, String regexStr, String suppleStr) {
        this.code = code;
        this.name = name;
        this.regexStr = regexStr;
        this.suppleStr = suppleStr;
    }

}
