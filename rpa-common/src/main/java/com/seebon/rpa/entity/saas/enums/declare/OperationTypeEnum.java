package com.seebon.rpa.entity.saas.enums.declare;

import lombok.Getter;

@Getter
public enum OperationTypeEnum {
    LOGON(1,"登录"),
    VERIFICATION(2,"核验"),
    ACCOUNT(3,"开户"),
    UNSEAL(4,"启封"),
    CHANGEINTO(5,"转入"),
    SAFEKEEPING(6,"封存"),
    PAYMENT(7,"补缴"),
    BASE(8,"基数调整"),
    OTHER(9,"其他");


    private Integer code;
    private String text;

    OperationTypeEnum(Integer code, String text){
        this.code = code;
        this.text = text;
    }

    public static String getValueByCode(Integer code){
        for(OperationTypeEnum platformFree:OperationTypeEnum.values()){
            if(code.equals(platformFree.getCode())){
                return platformFree.getText();
            }
        }
        return  null;
    }
}
