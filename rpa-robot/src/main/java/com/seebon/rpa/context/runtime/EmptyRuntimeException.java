package com.seebon.rpa.context.runtime;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LY
 * @date 2023/11/1 19:42
 */
@Slf4j
public class EmptyRuntimeException extends RuntimeException {

    private final String stepCode;

    public EmptyRuntimeException(String stepCode) {
        this.stepCode = stepCode;
    }

    public String getStepCode() {
        return stepCode;
    }
}
