package com.seebon.rpa.actions.target;

import java.util.Map;

public interface Target {

    /**
     * 获取.
     * 存储<名称,对象>,名称为Target对象的名称，如
     * key=element,value=WebElement
     * key=response,value=CloseableHttpResponse
     */
    Map<String, Object> fetch();

    /**
     * 释放
     */
    void release();
}
