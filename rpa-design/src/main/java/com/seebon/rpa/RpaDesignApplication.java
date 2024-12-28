package com.seebon.rpa;

import com.seebon.rpa.http.HttpBeanRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Import(HttpBeanRegistrar.class)
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableTransactionManagement
public class RpaDesignApplication {

    public static ConcurrentHashMap<String, ScheduledFuture> map = new ConcurrentHashMap<String, ScheduledFuture>();

    public static void main(String[] args) {
        SpringBeanHolder.setApplicationContext(SpringApplication.run(RpaDesignApplication.class, args));
    }
}
