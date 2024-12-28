package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1111", name = "回盘数据开始行数")
public enum CounterOfferCharLine {

    A("A",0),
    B("B",1),
    C("C",2),
    D("D",3),
    E("E",4),
    F("F",5),
    G("G",6),
    H("H",7),
    I("I",8),
    J("J",9),
    K("K",10),
    L("L",11),
    M("M",12),
    N("N",13),
    O("O",14),
    P("P",15),
    Q("Q",16),
    R("R",17),
    S("S",18),
    T("T",19),
    U("U",20),
    V("V",21),
    W("W",22),
    X("X",23),
    Y("Y",24),
    Z("Z",25);

    String name;
    Integer index;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    CounterOfferCharLine(String name, int index){
        this.name = name;
        this.index = index;
    }

}
