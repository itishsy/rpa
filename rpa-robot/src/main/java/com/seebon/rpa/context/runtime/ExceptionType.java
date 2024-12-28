package com.seebon.rpa.context.runtime;

public enum ExceptionType {
    business("自定义业务异常。"),
    stop("主动中止流程"),
    npe("空指针异常。"),
    timeout("执行超时。"),
    runtime("运行时异常。");

    String title;
    ExceptionType(String title){
        this.title = title;
    }
}
