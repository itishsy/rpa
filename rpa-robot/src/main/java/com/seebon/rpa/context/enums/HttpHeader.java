package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1057", name = "HTTP请求头")
public enum HttpHeader {
    cookie("Cookie"),
    Header("Header"),
    HeaderAndCookie("HeaderAndCookie"),
    ;

    String name;

    HttpHeader(String name) {
        this.name = name;
    }
}
