package com.seebon.rpa.entity.agent.enums;

/**
 * TODO
 *
 * @author zjf
 * @describe 解析的省市县的枚举
 * @date 2023-11-15 15:28
 */
public enum FiledCodeEnum{

    PROVINCE("10000029","省行政区划"),
    CITY("10000028","地市行政区划"),
    AREA("10000027","县区行政区划"),
    DOMICILE("10000015","户籍详细地址");

    private String code;

    private String name;


    FiledCodeEnum(String code,String name){
        this.code=code;
        this.name=name;
    }

    public String getCode(){
        return code;
    }

    public String getName(){
        return name;
    }


}
