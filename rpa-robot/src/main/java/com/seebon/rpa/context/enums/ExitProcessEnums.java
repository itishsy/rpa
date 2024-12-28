package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

/**
 * @author : 阿祥
 * @desc :
 * @date : 2023/12/8  17:21
 */
@DataDictKey(value = "3985", name = "条件")
public enum ExitProcessEnums {
    EQUALS("equals",0),
    CONTAINS("contains",1);

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

    ExitProcessEnums(String name,Integer index){
        this.name = name;
        this.index = index;
    }
}
