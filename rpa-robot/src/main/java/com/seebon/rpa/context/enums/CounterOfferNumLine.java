package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;



@DataDictKey(value = "1101", name = "回盘数据开始列索引")
public enum CounterOfferNumLine {

    one(1),
    two(2),
    three(3),
    four(4),
    five(5),
    six(6),
    seven(7),
    eight(8),
    nine(9),
    ten(10);

    Integer name;


    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    CounterOfferNumLine(Integer name){
        this.name = name;
    }

}
