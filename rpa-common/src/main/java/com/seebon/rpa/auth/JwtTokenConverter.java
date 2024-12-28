package com.seebon.rpa.auth;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

public class JwtTokenConverter extends JwtAccessTokenConverter {

    public Map<String, Object> decode(String token) {
        return super.decode(token);
    }
}
