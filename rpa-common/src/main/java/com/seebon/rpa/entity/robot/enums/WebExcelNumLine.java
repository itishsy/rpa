package com.seebon.rpa.entity.robot.enums;

public enum WebExcelNumLine {
    colA("A",0,"colA"),
    colB("B",1,"colB"),
    colC("C",2,"colC"),
    colD("D",3,"colD"),
    colE("E",4,"colE"),
    colF("F",5,"colF"),
    colG("G",6,"colG"),
    colH("H",7,"colH"),
    colI("I",8,"colI"),
    colJ("J",9,"colJ"),
    colK("K",10,"colK"),
    colL("L",11,"colL"),
    colM("M",12,"colM"),
    colN("N",13,"colN"),
    colO("O",14,"colO"),
    colP("P",15,"colP"),
    colQ("Q",16,"colQ"),
    colR("R",17,"colR"),
    colS("S",18,"colS"),
    colT("T",19,"colT"),
    colU("U",20,"colU"),
    colV("V",21,"colV"),
    colW("W",22,"colW"),
    colX("X",23,"colX"),
    colY("Y",24,"colY"),
    colZ("Z",25,"colZ"),
    colAA("AA",26,"colAA"),
    colAB("AB",27,"colAB"),
    colAC("AC",28,"colAC"),
    colAD("AD",29,"colAD"),
    colAE("AE",30,"colAE"),
    colAF("AF",31,"colAF"),
    colAG("AG",32,"colAG"),
    colAH("AH",33,"colAH"),
    colAI("AI",34,"colAI"),
    colAJ("AJ",35,"colAJ"),
    colAK("AK",36,"colAK"),
    colAL("AL",37,"colAL"),
    colAM("AM",38,"colAM"),
    colAN("AN",39,"colAN"),
    colAO("AO",40,"colAO"),
    colAP("AP",41,"colAP"),
    colAQ("AQ",42,"colAQ"),
    colAR("AR",43,"colAR"),
    colAS("AS",44,"colAS"),
    colAT("AT",45,"colAT"),
    colAU("AU",46,"colAU"),
    colAV("AV",47,"colAV"),
    colAW("AW",48,"colAW"),
    colAX("AX",49,"colAX"),
    colAY("AY",50,"colAY"),
    colAZ("AZ",51,"colAZ");

    String name;

    Integer index;

    String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    WebExcelNumLine(String name, Integer index, String type) {
        this.name = name;
        this.index = index;
        this.type = type;
    }
}
