package com.seebon.rpa.context.runtime;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LY
 * @date 2023/11/1 19:42
 * 对于拉取报盘或者核验报盘空数据正常退出
 */
@Slf4j
public class FinishRuntimeException extends RuntimeException {

    private final String stepCode;

    public FinishRuntimeException(String stepCode) {
        this.stepCode = stepCode;
    }

    public String getStepCode() {
        return stepCode;
    }
}
