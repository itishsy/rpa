package com.seebon.rpa.context.runtime;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 机器人运行时异常。执行异常策略
 */
@Getter
@Slf4j
public class RobotRuntimeException extends RuntimeException {
    private ExceptionType type;
    private String flow;
    private String step;

    public RobotRuntimeException(String message) {
        super(ExceptionType.business.title + (message == null ? "" : message));
        this.type = ExceptionType.business;
    }

    public RobotRuntimeException(ExceptionType type, String message) {
        super(type.title + (message == null ? "" : message));
        this.type = type;
    }

    public RobotRuntimeException(ExceptionType type, String flow, String step, String message) {
        super(type.title + "flow:" + flow + ",step:" + step + "." + (message == null ? "" : message));
        this.type = type;
    }
}
