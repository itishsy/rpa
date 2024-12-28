package com.seebon.rpa.config;

import com.seebon.rpa.auth.JwtTokenConverter;
import com.seebon.rpa.config.WorkPathConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Value("${jwt.signing-key:06dee6247edf11ea9f383863bb3b0c3c}")
    private String signingKey;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorityInterceptor()).addPathPatterns("/**");
    }

    @Bean
    public HandlerInterceptorAdapter authorityInterceptor() {
        return new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String session = request.getHeader("SESSION_RPA");
                return super.preHandle(request, response, handler);
            }
        };
    }

    /**
     * 下载路径
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/download/**")
                .addResourceLocations("file:" + WorkPathConfig.getWorkPath());
    }


    @Bean
    public JwtTokenConverter jwtTokenConverter() {
        //设置签名密钥
        JwtTokenConverter jwtTokenConverter = new JwtTokenConverter();
        jwtTokenConverter.setSigningKey(signingKey);
        return jwtTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenConverter());
    }

}