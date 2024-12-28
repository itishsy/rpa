package com.seebon.rpa.context.runtime;

import lombok.extern.slf4j.Slf4j;

/**
 * 运行中断流程
 */
@Slf4j
public class RobotInterruptException extends RuntimeException {

    public RobotInterruptException(){

    }

    public RobotInterruptException(String message){
        super(message);
    }

}
