package com.seebon.rpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication
public class RpaAuthApplication {

    public static void main(String[] args) {
        SpringBeanHolder.setApplicationContext(SpringApplication.run(RpaAuthApplication.class, args));
    }

}
