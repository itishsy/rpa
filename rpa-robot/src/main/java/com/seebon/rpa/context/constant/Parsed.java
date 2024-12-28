package com.seebon.rpa.context.constant;

/**
 * 参数解析策略
 */
public enum Parsed {

    /**
     * action创建时必须解析成功，否则报配置错误ConfigurationException
     */
    REQUIRED,

    /**
     * action创建时不可解析
     */
    NO,

    /**
     * action创建时尝试解析，不成功不报错（默认）
     */
    TRY
}
