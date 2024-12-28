package com.seebon.rpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduleConfig {
    private static final Integer poolSize = Runtime.getRuntime().availableProcessors() * 2;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadNamePrefix("robot-runner-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        return taskScheduler;
    }
}
