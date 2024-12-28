package com.seebon.rpa.config;

import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.http.HttpRequestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RequestTokenConfig implements HttpRequestConfig {

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + SecurityContext.currentToken());
        return headers;
    }
}
