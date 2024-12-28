package com.seebon.rpa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 配置全局跨域
 *
 * @author xfz
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*"); // 1允许任何域名使用
        config.addAllowedHeader("*"); // 2允许任何头
        config.addAllowedMethod("*"); // 3允许任何方法（post、get等）
        config.setAllowCredentials(true);// 设置跨域访问可以携带cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); //注册
        return new CorsFilter(source);
    }
}
