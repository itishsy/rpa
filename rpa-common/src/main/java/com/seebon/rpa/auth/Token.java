package com.seebon.rpa.auth;

import lombok.Data;

@Data
public class Token {
    String access_token;
    String token_type;
    Long expires_in;
    String scope;
    String name;
    String custId;
    String userId;
    String[] authorities;
    String jti;
}
