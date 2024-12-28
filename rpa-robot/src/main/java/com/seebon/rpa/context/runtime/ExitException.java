package com.seebon.rpa.context.runtime;


import lombok.extern.slf4j.Slf4j;

/**
 *  用抛出异常的方式退出流程，包括主流程
 */
@Slf4j
public class ExitException extends RuntimeException {

    public ExitException(String message){
        super(message);
    }
}
