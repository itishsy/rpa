package com.seebon.rpa.context.runtime;

import lombok.extern.slf4j.Slf4j;

/**
 * 运行时跳转
 */

@Slf4j
public class RuntimeSkipTo extends RuntimeException {

    private final String stepName ;

    public RuntimeSkipTo(String stepName){
        this.stepName = stepName;
    }

    public String getStepName() {
        return stepName;
    }
}
