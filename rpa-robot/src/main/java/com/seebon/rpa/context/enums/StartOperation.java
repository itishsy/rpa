package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1058", name = "启动操作")
public enum StartOperation {
    openIntercept("开启拦截"),
    openInterceptAndDebug("开启拦截并禁用debug模式"),
    ;

    String name;

    StartOperation(String name) {
        this.name = name;
    }
}
