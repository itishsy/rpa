package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;


@DataDictKey(value = "1901", name = "报盘映射Excel列")
public enum WebExcelNumLine {

    colA("A",0),
    colB("B",1),
    colC("C",2),
    colD("D",3),
    colE("E",4),
    colF("F",5),
    colG("G",6),
    colH("H",7),
    colI("I",8),
    colJ("J",9),
    colK("K",10),
    colL("L",11),
    colM("M",12),
    colN("N",13),
    colO("O",14),
    colP("P",15),
    colQ("Q",16),
    colR("R",17),
    colS("S",18),
    colT("T",19),
    colU("U",20),
    colV("V",21),
    colW("W",22),
    colX("X",23),
    colY("Y",24),
    colZ("Z",25),
    colAA("AA",26),
    colAB("AB",27),
    colAC("AC",28),
    colAD("AD",29),
    colAE("AE",30),
    colAF("AF",31),
    colAG("AG",32),
    colAH("AH",33),
    colAI("AI",34),
    colAJ("AJ",35),
    colAK("AK",36),
    colAL("AL",37),
    colAM("AM",38),
    colAN("AN",39),
    colAO("AO",40),
    colAP("AP",41),
    colAQ("AQ",42),
    colAR("AR",43),
    colAS("AS",44),
    colAT("AT",45),
    colAU("AU",46),
    colAV("AV",47),
    colAW("AW",48),
    colAX("AX",49),
    colAY("AY",50),
    colAZ("AZ",51);

    String name;

    Integer index;


    public String getName() {
        return name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    WebExcelNumLine(String name,Integer index){
        this.name = name;
        this.index = index;
    }

}
