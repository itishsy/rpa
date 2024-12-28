package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/11/15  18:35
 */
public enum OperationType {

    login("登录", 1),
    verification("核验", 2),
    openAnAccount("开户", 3),
    unseal("启封", 4),
    toChangeInto("转入", 5),
    sealUpForSafekeeping("封存", 6),

    supplementaryPayment("补缴", 7),
    baseAdjustment("基数调整", 8),
    other("其他", 9);

    String name;

    Integer type;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    OperationType(String name, Integer type) {
        this.name = name;
        this.type = type;
    }
}
