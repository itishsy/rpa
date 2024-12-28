package com.seebon.rpa.repository.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan(basePackages = "com.seebon.rpa.repository.mapper")
public class MybatisConfig {
}
