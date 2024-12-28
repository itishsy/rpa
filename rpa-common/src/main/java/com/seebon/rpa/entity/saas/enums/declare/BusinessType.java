package com.seebon.rpa.entity.saas.enums.declare;

public enum BusinessType {

    allInsurance("全险", 1,"10007001","null","allInsurance"),
    social("社保", 1,"10007002","1001001","social"),
    medicare("医保", 1,"10007003","null","medicare"),
    singleWorkInjury("单工伤", 1,"10007004","null","singleWorkInjury"),
    workInjury("工伤系统", 1,"10007005","null","workInjury"),
    filingSystem("备案系统", 1,"10007006","null","filingSystem"),

    accfund("公积金", 2,"10008001","1001002","accfund");

    String name;

    Integer type;

    String tplCode;

    String dataBaseCode;

    String code;

    public String getTplCode() {
        return tplCode;
    }

    public String getCode() {
        return code;
    }

    public String getDataBaseCode() {
        return dataBaseCode;
    }

    public void setTplCode(String tplCode) {
        this.tplCode = tplCode;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDataBaseCode(String dataBaseCode) {
        this.dataBaseCode = dataBaseCode;
    }

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

    BusinessType(String name, Integer type, String tplCode,String dataBaseCode,String code) {
        this.name = name;
        this.type = type;
        this.tplCode = tplCode;
        this.dataBaseCode = dataBaseCode;
        this.code = code;
    }

    public static String getDatabaseCodeByName(String dataBaseCode) {
        BusinessType[] values = BusinessType.values();
        for (BusinessType item : values) {
            if (item.getDataBaseCode().equals(dataBaseCode)) {
                return item.getName();
            }
        }
        return null;
    }
}