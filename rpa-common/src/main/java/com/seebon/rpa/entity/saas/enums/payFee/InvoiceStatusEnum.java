package com.seebon.rpa.entity.saas.enums.payFee;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-12 14:36
 */
public enum  InvoiceStatusEnum{
    NotINVOICE(0, "未开票"),
    INVOICE(1, "已开票"),
    BFINVOICE(2, "部分开票");

    private Integer code;
    private String name;

    InvoiceStatusEnum(Integer code, String name) {
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
        InvoiceStatusEnum[] values = InvoiceStatusEnum.values();
        for (InvoiceStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return "";
    }

    public static InvoiceStatusEnum getEnumByCode(Integer code) {
        InvoiceStatusEnum[] values = InvoiceStatusEnum.values();
        for (InvoiceStatusEnum item : values) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
