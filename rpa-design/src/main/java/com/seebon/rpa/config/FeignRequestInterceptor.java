package com.seebon.rpa.config;

import com.seebon.rpa.auth.SecurityContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate temp) {
        temp.header(HttpHeaders.AUTHORIZATION, "Bearer " + SecurityContext.currentToken());
    }
}

