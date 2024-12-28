package com.seebon.rpa;

import com.seebon.rpa.http.HttpBeanRegistrar;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import(HttpBeanRegistrar.class)
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class RpaRobotApplication {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(RpaRobotApplication.class);
        SpringBeanHolder.setApplicationContext(builder.headless(false).run(args));
    }
}
