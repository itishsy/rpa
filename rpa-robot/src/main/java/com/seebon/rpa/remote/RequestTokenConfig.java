package com.seebon.rpa.remote;

import com.seebon.rpa.http.HttpRequestConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RequestTokenConfig implements HttpRequestConfig {

    @Value("${rpa.token}")
    private String token;

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }
}
