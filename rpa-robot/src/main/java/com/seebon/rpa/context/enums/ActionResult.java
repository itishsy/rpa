package com.seebon.rpa.context.enums;


import com.seebon.rpa.context.annotation.DataDictKey;

@DataDictKey(value = "1009", name = "操作结果")
public enum ActionResult {
    success("成功"),
    failed("失败");

    String name;
    ActionResult(String name){
        this.name = name;
    }
}
