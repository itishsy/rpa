package com.seebon.rpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

@Configuration
public class RedisLockRegistryConfig {

    @Bean(name = "redisLockRegistry")
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory factory) {
        return new RedisLockRegistry(factory, "rpa-design-lock", 5 * 60 * 1000L);
    }
}
