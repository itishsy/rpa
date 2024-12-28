package com.seebon.rpa.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class WorkPathConfig {

    private static String basePath;
    private static int workCacheTimeout;

    @Value("${work.path:}")
    public void setBasePath(String basePath) {
        if(StringUtils.isNotBlank(basePath)) {
            WorkPathConfig.basePath = basePath;
        } else {
            WorkPathConfig.basePath = System.getProperty("user.dir");
        }
    }

    @Value("${work.cache.timeout:30}")
    public void setWorkCacheTimeout(int workCacheTimeout) {
        WorkPathConfig.workCacheTimeout = workCacheTimeout;
    }

    /**
     * 缓存文件保留时间（天）
     */
    public static int getWorkCacheTimeout() {
        return workCacheTimeout;
    }

    public static String getWorkPath() {
        return WorkPathConfig.basePath + File.separator ;
    }

    /**
     * 模板文件
     */
    public static String getWorkTpl() {
        return getWorkPath() + File.separator + "tpl";
    }

    /**
     * Jar包
     */
    public static String getWorkJar() {
        return getWorkPath() + File.separator + "jar";
    }

    /**
     * 缓存文件
     */
    public static String getWorkCache() {
        return getWorkPath() + File.separator + "cache";
    }

}
