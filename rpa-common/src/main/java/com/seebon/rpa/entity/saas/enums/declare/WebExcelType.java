package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/11/10  18:10
 */
public enum WebExcelType {

    input("input"),//input标签
    select("select"),//select标签
    li("li"),//li标签
    ;

    private String type;

    WebExcelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
