package com.seebon.rpa.common.security;

/**
 * Security Constant
 */
public class SecurityConstant {

    public static final String OAUTH_SERVER_NAME = "${app.oauth.name}";
    public static final String TOKEN_URL = "/oauth/token";
    public static final String RESOURCES = "resources";
    public static final String AUTHORITIES = "authorities";
    public static final String PASSWORD_RESET_URL = "/password/reset";
    public static final String USER_ADD_URL = "/user/add";
    public static final String PASSWORD_CHECK_URL = "/password/check";

    public static final long TOKEN_EXPIRE_TIME = 24 * 60 * 60;
}
