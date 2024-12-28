package com.seebon.rpa.common.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan("com.seebon.rpa.repository.mysql")
public class MybatisConfig {
}
