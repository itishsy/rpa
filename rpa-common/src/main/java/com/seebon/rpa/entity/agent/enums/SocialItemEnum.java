package com.seebon.rpa.entity.agent.enums;

/**
 * @author ZhenShijun
 * @dateTime 2022-10-25 16:13:44
 */
public enum SocialItemEnum {

    YangLao("10003001", "40000001", "养老"),
    GongShang("10003002", "40000002", "工伤"),
    YiLiao("10003003", "40000003", "医疗"),
    ShengYu("10003004", "40000004", "生育"),
    ShiYe("10003005", "40000005", "失业"),
    ZhongJi("10003006", "40000006", "重疾"),
    BuChongYiLiao("10003007", "40000012", "补充医疗"),
    DaBinYiLiao("10003008", "40000013", "大病医疗");


    private String itemCode;

    private String dataSourceItemCode;

    private String name;


    SocialItemEnum(String itemCode, String dataSourceItemCode, String name) {
        this.itemCode = itemCode;
        this.dataSourceItemCode = dataSourceItemCode;
        this.name = name;
    }

    public static SocialItemEnum getEnumByDataSourceItemCode(String dataSourceItemCode) {
        SocialItemEnum[] values = SocialItemEnum.values();
        for (SocialItemEnum item : values) {
            if (item.getDataSourceItemCode().equals(dataSourceItemCode)) {
                return item;
            }
        }
        return null;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getDataSourceItemCode() {
        return dataSourceItemCode;
    }

    public String getName() {
        return name;
    }
}
