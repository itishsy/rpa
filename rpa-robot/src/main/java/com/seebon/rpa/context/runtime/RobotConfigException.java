package com.seebon.rpa.context.runtime;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置类的错误，中止主流程。
 */
@Slf4j
public class RobotConfigException extends RuntimeException {

    public static final String title = "机器人配置错误。";

    public RobotConfigException(String message) {
        super(title + message);
    }

}
