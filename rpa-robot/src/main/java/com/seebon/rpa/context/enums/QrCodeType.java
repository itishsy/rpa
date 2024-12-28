package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1970", name = "保存二维码方式")
public enum QrCodeType {

    Screenshot("截图"),
    Download("下载");

    String name;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    QrCodeType(String name) {
        this.name = name;
    }

}
