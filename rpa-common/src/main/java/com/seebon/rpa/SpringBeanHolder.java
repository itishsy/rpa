package com.seebon.rpa;

import org.springframework.context.ApplicationContext;

public class SpringBeanHolder {

    public static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBeanHolder.applicationContext = applicationContext;
    }

    /**
     * get bean by name
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * get bean by class
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }


}
