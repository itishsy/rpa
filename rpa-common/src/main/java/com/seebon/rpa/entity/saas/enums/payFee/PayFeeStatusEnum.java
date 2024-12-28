package com.seebon.rpa.entity.saas.enums.payFee;

public enum PayFeeStatusEnum {

    WeiHeDing(0, "未核定"),
    YiHeDing(1, "已核定"),
    YiJiaoFei(2, "已缴费"),
    JiaoFeiYiChang(3, "缴费异常");

    private Integer code;
    private String name;

    PayFeeStatusEnum(Integer code, String name) {
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
        PayFeeStatusEnum[] values = PayFeeStatusEnum.values();
        for (PayFeeStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static PayFeeStatusEnum getEnumByCode(Integer code) {
        PayFeeStatusEnum[] values = PayFeeStatusEnum.values();
        for (PayFeeStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

}
