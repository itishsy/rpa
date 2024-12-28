package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1003", name = "HTTP请求方式")
public enum HttpMethod {
    post("POST"),
    postHttps("POSTHTTPS"),
    postDeclare("拉取报盘"),
    postFile("POST文件"),
    getBrowser("浏览器GET"),
    get("GET"),
    empFile("员工附件"),
    formPost("表单提交");

    String name;
    HttpMethod(String name){
        this.name = name;
    }
}
