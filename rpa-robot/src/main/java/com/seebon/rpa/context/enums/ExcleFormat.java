package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;
import lombok.Getter;

@DataDictKey(value = "1063", name = "Excle格式")
@Getter
public enum ExcleFormat {
    XlSX("XlSX"),
    XLS("XLS"),
    ;

    String name;

    ExcleFormat(String name) {
        this.name = name;
    }
}