package com.seebon.rpa.common.security.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 自定义认证失败信息
 */
@Component
public class AuthenticationFailure implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ae) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<String, Object>();
        Throwable cause = ae.getCause();
        map.put("code", "401");
        map.put("msg", "认证失败");
        map.put("path", request.getServletPath());
        map.put("data", (cause instanceof InvalidTokenException) ? "Token无效或已过期" : ae.getMessage());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), map);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}
